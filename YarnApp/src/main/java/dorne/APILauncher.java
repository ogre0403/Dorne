package dorne;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.util.Pair;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


/**
 * Created by 1403035 on 2016/6/29.
 */
public class APILauncher extends ContainerLauncher {

    private static final Log LOG = LogFactory.getLog(CLILauncher.class);

    // YARN related parameters
    String yarnContainerHost;

    //Docker related parameters
    DockerClient docker;
    String dockerContainerID;
    String dockerImageName;
    String dockerContainerCmd;

    public APILauncher(Container yarnContainer,  DockerAppMaster dockerAppMaster){
        super(yarnContainer, dockerAppMaster);
        this.yarnContainerHost = yarnContainer.getNodeId().getHost();

        // create docker java client
        DockerClientConfig config = DockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://" + yarnContainerHost + ":2375")
                .build();
        this.docker = DockerClientBuilder.getInstance(config).build();

        // TODO: get docker container from parameter
        this.dockerImageName = "nginx";
    }

    @Override
    public void process(ContainerLaunchContext ctx) {
        // Start docker container using docker-java API
        CreateContainerResponse dockerContainer =
                docker.createContainerCmd(this.dockerImageName).exec();
        this.dockerContainerID = dockerContainer.getId();
        docker.startContainerCmd(this.dockerContainerID).exec();

        try {
            dockerAppMaster.dockerContainerList.put(
                    new Pair<>(this.dockerContainerID, this.yarnContainerHost));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // attach to a running container using docker CLI
        ctx.setLocalResources(buildContainerLocalResource());
        List<String> commands = new ArrayList<String>();
        commands.add(buildContainerCmd());
        ctx.setCommands(commands);
        containerListener.addContainer(container.getId(), container);
        dockerAppMaster.getNMClientAsync().startContainerAsync(container, ctx);
    }



    private  String buildContainerCmd(){
        Vector<CharSequence> vargs = new Vector<>();
        vargs.add("bash");
        vargs.add(DorneConst.DOREN_LOCALRESOURCE_SCRIPT);
        vargs.add(this.dockerContainerID);
        StringBuilder command = new StringBuilder();
        for (CharSequence str : vargs) {
            command.append(str).append(" ");
        }
        return command.toString();
    }

    private boolean isDockerContainerRunning(DockerClient docker, String containerID){
        InspectContainerResponse ir;
        try {
            ir = docker.inspectContainerCmd(containerID).exec();
        }catch (Exception e){
            LOG.warn("Docker container with ID { " + dockerContainerID + " } is not found...");
            return false;
        }
        return ir.getState().getRunning();
    }
}