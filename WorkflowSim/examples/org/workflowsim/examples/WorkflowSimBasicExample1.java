/**
 * Copyright 2012-2013 University Of Southern California
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.workflowsim.examples;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.HarddriveStorage;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.workflowsim.CondorVM;
import org.workflowsim.Task;
import org.workflowsim.WorkflowDatacenter;
import org.workflowsim.Job;
import org.workflowsim.WorkflowEngine;
import org.workflowsim.WorkflowPlanner;
import org.workflowsim.utils.ClusteringParameters;
import org.workflowsim.utils.OverheadParameters;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.ReplicaCatalog;
import org.workflowsim.utils.Parameters.ClassType;
import java.io.FileWriter;
import java.io.PrintWriter;
import org.workflowsim.Job;

/**
 * This WorkflowSimExample creates a workflow planner, a workflow engine, and
 * one schedulers, one data centers and 20 vms. You should change daxPath at
 * least. You may change other parameters as well.
 *
 * @author Weiwei Chen
 * @since WorkflowSim Toolkit 1.0
 * @date Apr 9, 2013
 */
public class WorkflowSimBasicExample1 {

    protected static List<CondorVM> createVM(int userId, int vms) {
        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<CondorVM> list = new LinkedList<>();

        long baseSize = 10000; // image size (MB)
        int baseRam = 512;     // base RAM (MB)
        int baseMips = 1000;   // base MIPS
        long baseBw = 1000;    // base bandwidth
        int pesNumber = 1;     // number of cpus
        String vmm = "Xen";    // VMM name

        Random rand = new Random();

        // Create VMs with random parameters
        for (int i = 0; i < vms; i++) {
            int mips = baseMips + rand.nextInt(1000);     
            int ram = baseRam + rand.nextInt(1024);     
            long bw = baseBw + rand.nextInt(1000);       
            long size = baseSize + rand.nextInt(10000);  
            int pesNum = pesNumber + rand.nextInt(3);

            CondorVM vm = new CondorVM(i, userId, mips, pesNum, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());

            list.add(vm);
        }
        return list;
    }

    ////////////////////////// STATIC METHODS ///////////////////////
    /**
     * Creates main() to run this example This example has only one datacenter
     * and one storage
     */
    public static void main(String[] args) {
        try {
            int refNumber = 22;
            String daxFolderPath = "D:/Resource_Schedular/WorkflowSim/config/dax";
            File daxFolder = new File(daxFolderPath);
            File[] daxFiles = daxFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));

            //Initialize file headers for csv outputs
            List<PrintWriter> outputFiles = CreateOutputFiles();

            for(File CurrDaxFile : daxFiles) {
                for(int i=0; i<10; i++) {
                // First step: Initialize the WorkflowSim package.
                refNumber++;
                String ref = String.format("HEFT%05d", refNumber);
                Random rand = new Random();
                int vmNum = 7 + rand.nextInt(5); //number of vms

                String daxPath = String.join("/",daxFolderPath,CurrDaxFile.getName());
                Log.printLine(daxPath);
                File daxFile = new File(daxPath);
                if (!daxFile.exists()) {
                    Log.printLine("Warning: Please replace daxPath with the physical path in your working environment!");
                    return;
                }
                
                //Write to Input_Main.csv
                outputFiles.get(0).println(ref+","+vmNum+","+daxFile);

                Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.STATIC;
                Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.HEFT;
                ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.SHARED;
                
                /**
                 * No overheads
                 */
                OverheadParameters op = new OverheadParameters(0, null, null, null, null, 0);

                /**
                 * No Clustering
                 */
                ClusteringParameters.ClusteringMethod method = ClusteringParameters.ClusteringMethod.NONE;
                ClusteringParameters cp = new ClusteringParameters(0, 0, method, null);

                /**
                 * Initialize static parameters
                 */
                Parameters.init(vmNum, daxPath, null,
                        null, op, cp, sch_method, pln_method,
                        null, 0);
                ReplicaCatalog.init(file_system);

                // before creating any entities.
                int num_user = 1;   // number of grid users
                Calendar calendar = Calendar.getInstance();
                boolean trace_flag = true;  // mean trace events

                // Initialize the CloudSim library
                CloudSim.init(num_user, calendar, trace_flag);
                WorkflowDatacenter datacenter0 = createDatacenter("Datacenter_0");
                WorkflowPlanner wfPlanner = new WorkflowPlanner("Planner_0", 1);
                /**
                 * Create a WorkflowEngine.
                 */
                WorkflowEngine wfEngine = wfPlanner.getWorkflowEngine();
                /**
                 * Create a list of VMs.The userId of a vm is basically the id of
                 * the scheduler that controls this vm.
                 */
                List<CondorVM> vmlist0 = createVM(wfEngine.getSchedulerId(0), Parameters.getVmNum());

                for(CondorVM vm: vmlist0) {
                    outputFiles.get(1).println(
                        ref + "," +
                        vm.getId() + "," +
                        vm.getSize() + "," +
                        vm.getRam() + "," +
                        vm.getMips() + "," +
                        vm.getBw() + "," +
                        vm.getNumberOfPes()
                    );
                }

                /**
                 * Submits this list of vms to this WorkflowEngine.
                 */
                wfEngine.submitVmList(vmlist0, 0);

                /**
                 * Binds the data centers with the scheduler.
                 */
                wfEngine.bindSchedulerDatacenter(datacenter0.getId(), 0);
                CloudSim.startSimulation();
                List<Job> outputList0 = wfEngine.getJobsReceivedList();
                CloudSim.stopSimulation();

                List<Job> jobList = wfEngine.getJobsReceivedList();

                    try {
                        for (Job job : jobList) {
                            StringBuilder taskIds = new StringBuilder();
                            for (Task task : job.getTaskList()) {
                                taskIds.append(task.getCloudletId()).append(";");
                            }
                            // Remove trailing semicolon
                            if (taskIds.length() > 0) {
                                taskIds.setLength(taskIds.length() - 1);
                            }

                            outputFiles.get(2).println(
                                ref + "," +
                                job.getCloudletId() + "," +
                                "\"" + taskIds + "\"," +  // wrap in quotes to handle semicolons
                                job.getResourceId() + "," +
                                job.getVmId() + "," +
                                job.getDepth() + "," +
                                job.getActualCPUTime() + "," +
                                job.getExecStartTime() + "," +
                                job.getFinishTime() 
                            );
                        }

                        for (Job job : jobList) {
                            double net = job.getInputSize() + job.getOutputSize();
                            Log.printLine("Job " + job.getCloudletId() + " network usage: " + net + " bytes");
                        }

                    } catch (Exception e) {
                        Log.printLine("Training data could not be saved to Output_HEFT.csv");
                        e.printStackTrace();
                    }
                printJobList(outputList0);
                }
            }
        } catch (Exception e) {
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }

    protected static WorkflowDatacenter createDatacenter(String name) {

        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store one or more
        //    Machines
        List<Host> hostList = new ArrayList<>();

        // 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
        //    create a list to store these PEs before creating
        //    a Machine.
        for (int i = 1; i <= 20; i++) {
            List<Pe> peList1 = new ArrayList<>();
            int mips = 10000;
            // 3. Create PEs and add these into the list.
            //for a quad-core machine, a list of 4 PEs is required:
            peList1.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
            peList1.add(new Pe(1, new PeProvisionerSimple(mips)));

            int hostId = 0;
            int ram = 16384; //host memory (MB)
            long storage = 1000000; //host storage
            int bw = 10000;
            hostList.add(
                    new Host(
                            hostId,
                            new RamProvisionerSimple(ram),
                            new BwProvisionerSimple(bw),
                            storage,
                            peList1,
                            new VmSchedulerTimeShared(peList1))); // This is our first machine
            //hostId++;
        }

        // 4. Create a DatacenterCharacteristics object that stores the
        //    properties of a data center: architecture, OS, list of
        //    Machines, allocation policy: time- or space-shared, time zone
        //    and its price (G$/Pe time unit).
        String arch = "x86";      // system architecture
        String os = "Linux";          // operating system
        String vmm = "Xen";
        double time_zone = 10.0;         // time zone this resource located
        double cost = 3.0;              // the cost of using processing in this resource
        double costPerMem = 0.05;		// the cost of using memory in this resource
        double costPerStorage = 0.1;	// the cost of using storage in this resource
        double costPerBw = 0.1;			// the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<>();	//we are not adding SAN devices by now
        WorkflowDatacenter datacenter = null;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

        // 5. Finally, we need to create a storage object.
        /**
         * The bandwidth within a data center in MB/s.
         */
        int maxTransferRate = 15;// the number comes from the futuregrid site, you can specify your bw

        try {
            // Here we set the bandwidth to be 15MB/s
            HarddriveStorage s1 = new HarddriveStorage(name, 1e12);
            s1.setMaxTransferRate(maxTransferRate);
            storageList.add(s1);
            datacenter = new WorkflowDatacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datacenter;
    }

    /**
     * Creates output files and returns the printwriter
     * 
     * @param list
     */
    protected static List<PrintWriter> CreateOutputFiles() {
        List<PrintWriter> pwList = new ArrayList<>();

    String[] fileNames = {
        "Input_Main.csv",
        "VM_Parameters.csv",
        "Output_HEFT.csv"
    };

    String[] headers = {
        "Ref_No,VM_No,DAX_Path",
        "Ref_No,VM_ID,ImgSize,VM_Memory,MIPs,Bandwidth,PES",
        "Ref_No,Job_ID,Task_ID,DataCenter_ID,VM_ID,Job_Depth,Actual_CPU_Time,Start_Time,Finish_Time"
    };

    for (int i = 0; i < fileNames.length; i++) {
        File file = new File(fileNames[i]);
        boolean writeHeader = !file.exists() || file.length() == 0;

        try {
            PrintWriter pw = new PrintWriter(new FileWriter(file, true), true);
            if (writeHeader) {
                pw.println(headers[i]);
            }
            pwList.add(pw);
        } catch (Exception e) {
            Log.printLine("Error occurred while writing to " + fileNames[i]);
            e.printStackTrace();
        }
    }
        return pwList;
    }

    /**
     * Prints the job objects
     *
     * @param list list of jobs
     */
    protected static void printJobList(List<Job> list) {
        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Job ID" + indent + "Task ID" + indent + "STATUS" + indent
                + "Data center ID" + indent + "VM ID" + indent + indent
                + "Time" + indent + "Start Time" + indent + "Finish Time" + indent + "Depth");
        DecimalFormat dft = new DecimalFormat("###.##");
        for (Job job : list) {
            Log.print(indent + job.getCloudletId() + indent + indent);
            if (job.getClassType() == ClassType.STAGE_IN.value) {
                Log.print("Stage-in");
            }
            for (Task task : job.getTaskList()) {
                Log.print(task.getCloudletId() + ",");
            }
            Log.print(indent);

            if (job.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");
                Log.printLine(indent + indent + job.getResourceId() + indent + indent + indent + job.getVmId()
                        + indent + indent + indent + dft.format(job.getActualCPUTime())
                        + indent + indent + dft.format(job.getExecStartTime()) + indent + indent + indent
                        + dft.format(job.getFinishTime()) + indent + indent + indent + job.getDepth());
            } else if (job.getCloudletStatus() == Cloudlet.FAILED) {
                Log.print("FAILED");
                Log.printLine(indent + indent + job.getResourceId() + indent + indent + indent + job.getVmId()
                        + indent + indent + indent + dft.format(job.getActualCPUTime())
                        + indent + indent + dft.format(job.getExecStartTime()) + indent + indent + indent
                        + dft.format(job.getFinishTime()) + indent + indent + indent + job.getDepth());
            }
        }
    }
}
