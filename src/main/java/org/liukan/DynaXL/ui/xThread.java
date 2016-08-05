package org.liukan.DynaXL.ui;

import org.apache.commons.exec.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by liuk on 16-7-24.
 */
public class xThread extends Thread {
    private String XplorPath,WorkSpaceDir;
    private ExecuteWatchdog watchdog;

    public void end(){
        watchdog.destroyProcess();
        String line = "/usr/bin/killall xplor";
        CommandLine cmdLine = CommandLine.parse(line);
        DefaultExecutor executor = new DefaultExecutor();
        try {
            executor.execute(cmdLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stop();
    }
    public xThread(String XplorPath,String WorkSpaceDir){
        this.XplorPath=XplorPath;
        this.WorkSpaceDir=WorkSpaceDir;
    }
    public void run() {
        try {
            ProcessBuilder pb =
                    new ProcessBuilder(XplorPath + File.separator + "xplor",
                            "-in", "isop_patch3.inp");
            Map<String, String> env = pb.environment();
            pb.directory(new File(WorkSpaceDir));
            File log = new File("log");
            Process p = pb.start();
            p.waitFor(); // Wait for the process to finish.
            System.out.println("isop_patch3 executed successfully");

            CommandLine cmdLine = new CommandLine(XplorPath + File.separator + "xplor");
            cmdLine.addArgument("-smp");
            cmdLine.addArgument(Integer.toString(Runtime.getRuntime().availableProcessors()));
            cmdLine.addArgument("-py");
            cmdLine.addArgument("EIN0_explicit_optimize2dx.py");
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

            watchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);
            Executor executor = new DefaultExecutor();
            //executor.setExitValue(1);
            executor.setWatchdog(watchdog);
            executor.setWorkingDirectory(new File(WorkSpaceDir));
            executor.execute(cmdLine, resultHandler);

            resultHandler.waitFor();

            System.out.println("PY executed successfully");
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
