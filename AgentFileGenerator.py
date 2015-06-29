with open("test/agents/agents.xml", "r") as agentsFile:
	data = agentsFile.read()

dataLines = data.split('\n')

inAgent = False
curAgentData = ""
agentFileStrings = []

inRelations = False
relations = ""

inConstraints = False
constraints = ""

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
		constraints += '\t' + line + '\n'
	if line == "</constraints>":
		inConstraints = False


for agent in agentFileStrings:
	agent[1] += '\n' + relations
	agent[1] += '\n' + constraints.replace(agent[0] + ":", '')
	agent[1] += "</agent>\n"

	agentFile = open(agent[0] + ".xml", "w")
	agentFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n\n" + agent[1])
	agentFile.close()


for agent in agentFileStrings:
	print agent[0]
	print agent[1]