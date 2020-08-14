import re
import xml.etree.cElementTree as ET
import os

print("# PEmbroider API Reference\n")
s = open("../docs/xml/classprocessing_1_1embroider_1_1_p_embroider_graphics.xml",'r').read()
open('tmp','w').write(re.sub(r'</?ref.*?>','',s))

tree = ET.ElementTree(file="tmp")
os.system('rm tmp')

sections = tree.iter("sectiondef")

hints = "## Hints\n| Type | Name | Default | Description |\n|---|---|---|---|\n";

for sec in sections:
	if "attrib" in sec.attrib['kind'] and "public" in sec.attrib['kind']:
		memdefs = sec.iter("memberdef")
		for defn in memdefs:
			typ = defn.find("type").text
			try:
				if not typ:
					typ = defn.find("type").find("ref").text
			except:
				typ = None
			name = defn.find("name").text;

			detail = defn.find("detaileddescription").find("para")
			if detail != None:
				detail = detail.text;

			if detail == None:
				continue

			default = defn.find("initializer").text.replace("=","")

			hints += "|"+typ+"| `"+name+"` |"+default+"|"+detail+"|\n"

		continue

	if "func" not in sec.attrib['kind']:
		continue
	memdefs = sec.iter("memberdef")
	for defn in memdefs:
		typ = defn.find("type").text
		try:
			if not typ:
				typ = defn.find("type").find("ref").text
		except:
			typ = None
		name = defn.find("name").text;

		detail = defn.find("detaileddescription").find("para")
		if detail != None:
			detail = detail.text;

		params = defn.iter("parameteritem")

		if detail == None:
			continue

		if detail.startswith("(internal use)") or detail.startswith("(advanced use)"):
			continue
		# print(typ,name,brief,detail)

		defstr = defn.find("definition").text.replace("processing.embroider.PEmbroiderGraphics.","");
		argstr = defn.find("argsstring").text
		print("### `"+defstr+argstr+"`")

		print("```")
		if detail != None:
			print(detail)
		print("```")

		print("|parameter|description|")
		print("|---|---|")
		for par in params:
			pname = par.find("parameternamelist").find("parametername").text
			pdesc = par.find("parameterdescription").find("para").text
			
			print("|`"+pname+"`|```"+pdesc+"```|")

		ret = defn.iter("simplesect")
		print("\n")
		for r in ret:
			try:
				print("**return** `"+r.find("para").text+"`\n")
			except:
				pass
		print("\n\n-----------------\n\n")


print(hints)