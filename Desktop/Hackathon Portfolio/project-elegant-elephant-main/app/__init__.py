import os
from flask import Flask, render_template, request
from dotenv import load_dotenv

load_dotenv()
app = Flask(__name__)


@app.route('/')
def index():
    return render_template('index.html', title="Home", url=os.getenv("URL"))

@app.route('/professionalinfo')
def professionInfo():
    return render_template('prof.html', title="Education/Experience", url=os.getenv("URL"))

@app.route('/hobbies')
def portfolio():
    return render_template('hobbies.html', title="Hobbies", url=os.getenv("URL"))