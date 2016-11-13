#basic shell with data for our project
#data is manually added until further notice
from flask import Flask, render_template, request
app = Flask(__name__)


# oppList is the defined as the current Opportunity List
totOpps = []
totUsers = []
userInterest = {}


# object posted: ["userName", "PhoneNumber", "address"]
@app.route('/addUser', methods=['POST'])
def addUser():
    username = request.form['username']
    phonenumber = request.form['phonenumber']
    address = request.form['address']
    age = request.form['age']
    totUsers.append([username, phonenumber, address, age])
    userInterest[username] = []
    return str(totUsers)


@app.route('/addInterest', methods=['POST'])
def addInterest(username):
    newinterest = request.form['newinterest']
    if username in userInterest:
        userInterest[username].append(newinterest)
    else:
        userInterest[username] = [newinterest]
    return str(newinterest)


@app.route('/curUserInterest', methods=['GET'])
def getInterests(username):
    return str(userInterest[username])


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
