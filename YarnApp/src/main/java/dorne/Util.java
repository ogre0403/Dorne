package dorne;

import org.apache.commons.cli.Options;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by 1403035 on 2016/5/20.
 */
public class Util {
    public static Options ClientOptions(){
        Options opts = AMOptions();
        opts.addOption(DorneConst.DOREN_OPTS_APPNAME, true,
                "Application Name. Default value - dorne");
        opts.addOption(DorneConst.DOREN_OPTS_YARN_AM_MEM, true,
                "Amount of memory in MB to run AM");
        opts.addOption(DorneConst.DOREN_OPTS_YARN_AM_CORE, true,
                "Amount of virtual cores to run AM");
        opts.addOption(DorneConst.DOREN_OPTS_JAR, true,
                "Jar file containing the application master");
        opts.addOption(DorneConst.DOREN_OPTS_DOCKER_SERVICE, true,
                "Prebuild dockerized service type");
        return opts;
    }

    public static Options AMOptions(){
        Options opts = new Options();
        opts.addOption(DorneConst.DOREN_OPTS_DOCKER_CONTAINER_NUM, true,
                "No. of containers on which the shell command needs to be executed");
        opts.addOption(DorneConst.DOREN_OPTS_DOCKER_CONTAINER_MEM, true,
                "Amount of memory in MB to be requested to run docker container");
        opts.addOption(DorneConst.DOREN_OPTS_DOCKER_CONTAINER_CORE, true,
                "Amount of core to be requested to run docker container");
        opts.addOption(DorneConst.DOREN_OPTS_DOCKER_SERVICE, true,
                "Prebuild dockerized service type");
        opts.addOption(DorneConst.DOREN_OPTS_DOCKER_SERVICE_ARGS, true,
                "dockerized service command arguments");

        return opts;
    }

    public static Options ThriftClientOption(){
        Options opts = new Options();
        opts.addOption("server", true, "thrift server IP");
        opts.addOption("port", true, "thrift server port");
        opts.addOption("operation", true, "add/remove/show");
        opts.addOption("num", true, "number of containers to add/remove");
        return opts;
    }

    public static int getAvailablePort() throws IOException {
        ServerSocket socket = null;
        int port = 0;
        try {
            socket = new ServerSocket(0);
            port = socket.getLocalPort();
        } finally {
            if (socket!=null)
                socket.close();
        }
        return port;
    }

}
