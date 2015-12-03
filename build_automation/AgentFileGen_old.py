import os, sys

targetFile = sys.argv[1]

with open(targetFile, "r") as agentsFile:
	data = agentsFile.read()

dataLines = data.split('\n')
numAgents = 0
agentFileStrings = ['']*10
variableString = "\t<variables nbVariables=\"1\">\n"
index=1
domainSection = "\t<domains nbDomains=\"1\">\n"
varSection = ""
neighborSection = ""
aname = ""
port = 2552
relCount = 0
relSection =''
nbCons = 0
cSection = ''
neighborNames = [""]*25
varNames = [""]*0
assocAgent = [""]*0
varCount = 0
neighborCount = 0



for line in dataLines:
	if  "<agents nbAgents=" in line:
		#this assumes number of agents is two digits, fix later
		numAgents = line[line.find("\"")+1:]
		print("num agents is 1: ")
		print(numAgents)
		numAgents = numAgents[:numAgents.find("\"")]
		print("num agents is ")
		print(numAgents)
		
for index in range(0, int(numAgents)): #for each agent file separately
	#if(index == numAgents):
	#break
	agentFileStrings[index] = agentFileStrings[index] + "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n\n<agent>\n"	
	print(agentFileStrings[0])
	#parsing the ident section
	for line in dataLines: #for each line in the original file.
		temp = "<agent name=\"a_" + str(index) + "\"/>"
		aname = "a_" + str(index)
		
		if temp in line:
			agentFileStrings[index] =	agentFileStrings[index] + "\t<id name=\"a_" + str(index) + "\" ip=\"127.0.0.1\" port=\"" + str(port) + "\" />\n"
		
		#parsing the domain section
		 
		temp = "<domain name=\""
		if temp in line:
			temp1 = line[0:31]
			temp2 = line[31:]
			print(temp1)
			domainSection = domainSection + "\t" + temp1 + " datatype = \"int\"" + temp2 +"\n\t</domains>\n"
			#domain section complete
			agentFileStrings[index] = agentFileStrings[index] + domainSection
		
		# time to do the variables!
		#assuming they all have one variable
		
		temp1 = "<variable name="
		temp2 = "a_" + str(index) + "\"/>"
		if temp1 in line:
			print("Got in first if")
			name = line[line.find("\"") + 1:] #saving the var name
			name = name[:name.find("\"")]
			scopeString = line[line.find("=")+1:]
			scopeString = scopeString[scopeString.find("=") +1:]
			scopeString = scopeString[scopeString.find("=") +1:]
			scopeString = scopeString[scopeString.find("\"") +1:]
			scopeString = scopeString[:scopeString.find("\"") ]
			vflag = 0
			for v in varNames:
				print("In addition for loop name is : " + name)
				if(v == name):
					vflag = 1  
			
			if(vflag == 0):
				print("adding array element name is " + name)
				varNames.append(name)
				assocAgent.append(scopeString)
				print(varNames)
				print(assocAgent)
			
			if temp2 in line:
				print("Got in second if")
				#do this 
				
				
				print("Scope string is " + scopeString)
				print("name is " + name)
				
				varSection = varSection + "\t\t<variable name=\"" + name + "\" domain=\"d\"/>\n"
				varCount = varCount + 1
				#<variable name="var1" domain="dom1" />	
		
		
		
		#time to get the relations
		#temp = "nbRelations"
		#if temp in line: #if this is the start of relation section
		#	nbRelations = line[line.find("\"") +1:]
		#	nbRelations = nbRelations[:nbRelations.find("\"")]
		#	relSection = relSection + "<relations nbRelations=\"" + nbRelations +"\">"	
		
		temp = "_" + str(index)
		temp1 = "relation"
		if temp in line: #if the relation applies to this agent
			if temp1 in line:
				relCount = relCount + 1
				name = ""
				arity = ""
				defaultCost = ""
				nbTuples = ""
				semantics = ""
				temp2 = line
				#extracting name
				temp2 = temp2[temp2.find('\"' ) + 1:] #trimming first quote
				name = temp2[:temp2.find('\"')]
				print("printing rname " + name)
				#extracting arity
				temp2 = temp2[temp2.find('\"') + 1: ] #trimming first quote.
				temp2 = temp2[temp2.find('\"' ) + 1:] #trimming first quote
				arity = temp2[: temp2.find('\"')]
				
				temp2 = temp2[temp2.find('\"' ) + 1:] #trimming first quote
				temp2 = temp2[temp2.find('\"' ) + 1:] #trimming first quote
				
				#extracting nbTuples
				
				nbTuples = temp2[: temp2.find("\"")]
				
				#extracting semantics
				
				temp2 = temp2[temp2.find("\"" ) + 1:] #trimming first quote
				temp2 = temp2[temp2.find("\"" ) + 1:] #trimming first quote
				semantics = temp2[: temp2.find("\"")]
				#extracting default cost
				
				temp2 = temp2[temp2.find("\"" ) + 1:] #trimming first quote
				temp2 = temp2[temp2.find("\"" ) + 1:] #trimming first quote
				defaultCost = temp2[:temp2.find("\"")]
				
				#now to get tuple data
				
				tuples = temp2[temp2.find(">") + 1:] #getting everything after the carrot
				tuples = tuples[:tuples.find("<" )-1]
				#now to build our relation line.
					
				relLine = "\t\t<relation name=\"" + name + "\" arity =\"" + arity + "\" defaultCost=\"" + defaultCost +"\" nbTuples=\"" + nbTuples + "\" semantics=\"" + semantics +"\">"
				relLine = relLine + "\n\t\t\t" + tuples +"\n" +"\t\t</relation>\n"
				
				relSection = relSection + relLine  
		
		#time to get constraints
		#<constraint name="c_0" arity="2" scope="v_1 v_0" reference="r_1_0"/>
				
		#<constraint name="con2" arity="3" scope="var1 agt2:var2 agt3:var1" reference="rel2"/>
		d = zip(assocAgent, varNames)
		temp = "<constraint name="
		temp1 = "v_" + str(index) + "\""
		temp2 ="v_" + str(index) + " "
		if temp in line:
			print("Got in con1")
			if (temp1 in line) or (temp2 in line): #we have found a constraint that pertains to our agent
				print("Got in constraint")
				nbCons = nbCons + 1
				t = line
				t = t[t.find('\"' ) + 1:] #trimming first quote
				name = t[:t.find('\"')]
				t = t[t.find("\"" ) + 1:] #trimming first quote
				t = t[t.find("\"" ) + 1:] #trimming first quote
				arity = t[:t.find('\"')]
				t = t[t.find("\"" ) + 1:] #trimming first quote
				t = t[t.find("\"" ) + 1:] #trimming first quote
				scope = t[: t.find('\"')]
		
				t = t[t.find("\"" ) + 1:] #trimming first quote
				t = t[t.find("\"" ) + 1:] #trimming first quote
				reference = t[: t.find('\"')]
			
				#now we have picked out the parts
				
				print("original Scope is " + scope)
				scopeStr = ""
				for j in range(0, int(arity)):
					if(j< int(arity) -1):
						vn = scope[:scope.find(" ")]#getting up to the first space
					else:
						vn = scope
					print("PRINTING VN " + vn)
					#if(j < int(arity) - 1): #if it is not the last one
					t=""
					scope = scope[scope.find(" ") + 1:]
					for l in range(0, len(varNames)):
						if(vn == varNames[l]):
							if(assocAgent[l] != aname):#if the var doesn't belong to current agent
								#taking care of adding to neighbors
								flag = 0
								for element in range(0, neighborCount+1) :
									if assocAgent[l] == neighborNames[element] :
										flag = 1
								if flag == 0 :
									neighborNames[neighborCount] = assocAgent[l]
									neighborCount = neighborCount + 1
								#now we take care of the scope string 
								t = assocAgent[l] + ":" + varNames[l]
							else:# this variable is ours
								t = varNames[l]
					scopeStr = scopeStr + t		
					if(j<int(arity)-1):
						scopeStr = scopeStr + " " 
					
							
						
					
						
						
						
					print("ScopeStr is " + scopeStr)
					print("scope is now " + scope)
				
				cSection =  cSection + "\t\t<constraint name=\"" + name + "\" arity=\"" + arity + "\" scope=\"" + scopeStr + "\" reference=\"" + reference + "\"/>\n" 
			
	varSection = "\t<variables nbVariables=\"" + str(varCount) + "\">\n" + varSection
	varCount = 0
	neighborSection = "\t<neighbors nbNeighbors = \"" + str(neighborCount) + "\">\n"
	for neigh in range(0, neighborCount):
		#make a new neighbor line
		ip = 2552 + int(neighborNames[neigh][neighborNames[neigh].find("_") + 1]) #getting the ip
		neighborSection = neighborSection +  "\t\t<neighbor name=\"" + neighborNames[neigh] + "\" ip=\"127.0.0.1\" port=\"" + str(ip) +"\" />\n"
	neighborSection = neighborSection + "\t</neighbors>\n"
	cSection = "\t<constraints nbConstraints =\"" + str(nbCons) + "\">\n" + cSection + "\t</constraints>\n"	
	print("got out of big loop")
	port = port + 1
	relSection = "\t<relations nbRelations=\"" + str(relCount) + "\">\n" + relSection
	relSection = relSection + 	"\t</relations>"
	varSection = varSection + "\t</variables>\n"
	agentFileStrings[index] = agentFileStrings[index] + varSection + "\n" + relSection + "\n" +  "\n" + cSection + "\n" + "\n" + neighborSection + "\n</agent>"
	print("got past after")
	print(agentFileStrings[index])
	agentFile = open("test/agents/a_" + str(index) + ".xml", "w")
	agentFile.write(str(agentFileStrings[index]))
	agentFile.close()
	neighborSection = ""
	cSection = ""
	domainSection = "\t<domains nbDomains=\"1\">\n"
	varSection = ""
	relSection = ""
	neighborCount = 0
	neighborNames = [""]*25
	
