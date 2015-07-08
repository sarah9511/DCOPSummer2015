with open("agents.xml", "r") as agentsFile:
	data = agentsFile.read()

dataLines = data.split('\n')

inAgent = False
curAgentData = ""
agentFileStrings = []

inRelations = False
relations = ""

inConstraints = False
constraints = []

for line in dataLines:
	if line == "<agent>":
		inAgent = True
	if "<id name=" in line:
		name = line[11:line.find("\" ip")]
	if line == "</agent>":
		inAgent = False
		agentFileStrings.append([name, curAgentData])
		curAgentData = ""
	if inAgent:
		curAgentData += line + '\n'

	if line.startswith("<relations"):
		inRelations = True
	if inRelations:
		relations += '\t' + line + '\n'
	if line == "</relations>":
		inRelations = False

	if line.startswith("<constraints"):
		inConstraints = True
	if inConstraints:
		constraints.append('\t' + line + '\n')
	if line == "</constraints>":
		inConstraints = False

for agent in agentFileStrings:
	agent[1] += '\n' + relations + '\n'

	agentConstraints = ""
	for line in constraints:
		if "<constraints" in line or "</constraints>" in line or agent[0] + ':' in line:
			agentConstraints += line.replace(agent[0] + ':', '')
	
	agent[1] += agentConstraints + '\n'

	neighborCount = 0
	neighbors = ""

	for agent2 in agentFileStrings:
		if agent2[0] + ':' in agentConstraints:
			neighborCount += 1
			idI = agent2[1].find("<id name=")
			neighbors += "\t\t<neighbor " + agent2[1][idI + 4:agent2[1].find("/>", idI) + 2] + '\n'

	agent[1] += "\t<neighbors nbNeighbors=\"" + str(neighborCount) + "\">\n" + neighbors + "\t</neighbors>\n"

	agent[1] += "</agent>\n"

	agentFile = open("test/agents/" + agent[0] + ".xml", "w")
	agentFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n\n" + agent[1])
	agentFile.close()

for agent in agentFileStrings:
	print agent[0]
	print agent[1]
