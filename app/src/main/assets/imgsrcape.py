import requests

with open('ids.txt', 'r') as infile:
	for id in infile:
		id = str(id).strip()
		print "Saving %s.gif" % id

		with open("img/%s.gif" % id, 'wb') as f:
			f.write(requests.get("http://services.runescape.com/m=itemdb_oldschool/obj_big.gif?id=%s" % id).content)
