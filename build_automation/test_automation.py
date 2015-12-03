

import sys
import os
import subprocess


numProblems = sys.argv[1]
outputPath = sys.argv[2]
numAgents = sys.argv[3]
dom_size = sys.argv[4]
p1 = sys.argv[5]
p2 = sys.argv[6]

probDirectory = "probs"

piips = ["172.24.45.110", "172.24.89.230", "172.24.36.25", "172.24.6.110"]

targetDir = ""

#remove extra file formats
def scandirs(path): 
    for root, dirs, files in os.walk(path):
        for currentFile in files:
            #print "processing file: " + currentFile
            exts = ('.xml', '.py')
            if any(currentFile.lower().endswith(ext) for ext in exts):
                print ("keeping " + currentFile)
                
            else: 
                os.remove(os.path.join(root, currentFile))
                #print("removing " + currentFile)

                  
            
#files will all begin with rep_[i]_ then use the name provided in the second parameter
filename_endings = outputPath[ outputPath.find("/") + 1:]




#call dcop_generator, remove extra formats
os.system("./dcop_generator " + numProblems + " " + outputPath + " -random " + numAgents + " " + dom_size + " " + p1 + " " + p2 );
scandirs(probDirectory)

#for all xml files:
#   call parser/separator
#   move output to appropriate new directory (send as parameter to modified AgentFileGen script)
#   change all instances of default ip addresses to correct ip addresses (hard coded in AgentFileGen for now)
#	remove original 
for i in range(0, int(numProblems)):
    toMove = probDirectory + "/rep_" + str(i) + "_" + filename_endings + ".xml"
    os.system("python " + probDirectory + "/AgentFileGen.py " + toMove + " " + "p_" + str(i) +filename_endings)
    
    
    os.remove(toMove)
    #break


#scp each problem directory to each pi
for i in range(0, int(numProblems)):
	targetDir= probDirectory + "/agents/" + "p_" + str(i) + filename_endings
	for j in range(0, int(numAgents)):
		os.system("scp -r " + targetDir + " pi@" + piips[j] + ":/home/pi/4_agents/test/agents" )

classpath = "\"/home/pi/4_agents/lib/akka-actor_2.11-2.3.9.jar:/home/pi/4_agents/lib/config-1.2.1.jar:/home/pi/4_agents/lib/protobuf-java-2.5.0.jar:/home/pi/4_agents/lib/akka-remote_2.11-2.3.9.jar:/home/pi/4_agents/lib/netty-3.8.0.Final.jar:/home/pi/4_agents/lib/scala-library-2.11.5.jar:/home/pi/4_agents/lib/mysql-connector-java-5.1.37-bin.jar:/home/pi/4_agents/bin:/home/pi/4_agents/test\""



#os.system("java -cp \"/home/pi/4_agents/lib/akka-actor_2.11-2.3.9.jar:/home/pi/4_agents/lib/config-1.2.1.jar:/home/pi/4_agents/lib/protobuf-java-2.5.0.jar:/home/pi/4_agents/lib/akka-remote_2.11-2.3.9.jar:/home/pi/4_agents/lib/netty-3.8.0.Final.jar:/home/pi/4_agents/lib/scala-library-2.11.5.jar:/home/pi/4_agents/lib/mysql-connector-java-5.1.37-bin.jar:/home/pi/4_agents/bin:/home/pi/4_agents/test\" smartgrids.AgentMonitor")

for i in range(0, int(numProblems)): #for each problem
	for j in range(0, int(numAgents)): #for each agent
		procCommand = "ssh pi@" + piips[0] + " java -cp " + classpath + " smartgrids.AgentGenerator " + "/home/pi/4_agents/test/agents/p_" + str(i) + filename_endings + "/a_" + str(j) + ".xml"   
		#print(procCommand)
		os.system(procCommand + " &")


