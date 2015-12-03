

import sys
import os

numProblems = sys.argv[1]
outputPath = sys.argv[2]
numAgents = sys.argv[3]
dom_size = sys.argv[4]
p1 = sys.argv[5]
p2 = sys.argv[6]

probDirectory = "probs"

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
#   change all instances of default ip addresses to correct ip addresses
#	remove original 
for i in range(0, int(numProblems)):
    toMove = probDirectory + "/rep_" + str(i) + "_" + filename_endings + ".xml"
    os.system("python " + probDirectory + "/AgentFileGen.py " + toMove + " " + "p_" + str(i) +filename_endings)
    
    
    os.remove(toMove)
    #break
