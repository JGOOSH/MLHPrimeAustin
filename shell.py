# basic shell with data for our project
# data is manually added until further notice
from flask import Flask, render_template, request
app = Flask(__name__)


# oppList is the defined as the current Opportunity List
totOpps = []
totUsers = {}


# object posted: ["userName", "PhoneNumber", "address"]
@app.route('/addUser', methods=['POST'])
def addUser():
    username = request.form['username']
    name = request.form['name']
    phonenumber = request.form['phonenumber']
    address = request.form['address']
    age = request.form['age']
    bio = request.form['bio']
    totUsers[username] = [name, phonenumber, address, age, bio, []]
    return str(totUsers)


@app.route('/addInterest', methods=['POST'])
def addInterest():
    username = request.form['username']
    newinterest = request.form['newinterest']
    if username in totUsers:
        totUsers[username][5].append(newinterest)
    else:
        return "username " + username + " does not exist"
    return str(newinterest)


@app.route('/curUser', methods=['GET'])
def getUsers():
    return str(totUsers)


@app.route('/curOpp', methods=['GET'])
def getOpps():
    return str(totOpps)


@app.route('/appOpp', methods=['POST'])
def returnOpps():
    curOpp = str(request.form["opportunity"])
    curDate = str(request.form["date"])
    curLocation = str(request.form["location"])
    curNeeded = str(request.form["needed"])
    totOpps.append([curOpp, curDate, curLocation, curNeeded])
    return str(totOpps)


if __name__ == "__main__":
    app.run()
