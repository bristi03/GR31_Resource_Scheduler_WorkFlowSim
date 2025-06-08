from flask import Flask, request, jsonify
import joblib
import numpy as np

app = Flask(__name__)

# Load your trained model
model = joblib.load("model/execution_time_predictor.pkl")

@app.route('/predict', methods=['POST'])
def predict():
    data = request.json
    try:
        features = [
            data["TaskMI"],
            data["TaskSize"],
            data["Job_Depth"],
            data["MIPs"],
            data["Bandwidth"],
            data["VM_Memory"],
            data["PES"],
            data["TaskMI_per_MIPS"]
        ]
        features_array = np.array(features).reshape(1, -1)
        predicted_time = model.predict(features_array)[0]
        return jsonify({"predicted_time": float(predicted_time)})
    except Exception as e:
        return jsonify({"error": str(e)}), 400

@app.route('/')
def index():
    return """
    <h2>Hybrid HEFT Execution Time Prediction API</h2>
    <p>This Flask server provides a REST API to predict the execution time of workflow tasks on virtual machines using a trained machine learning model.</p>
    <ul>
        <li><strong>Endpoint:</strong> <code>/predict</code></li>
        <li><strong>Method:</strong> POST</li>
        <li><strong>Expected Input:</strong> JSON with task and VM features</li>
        <li><strong>Output:</strong> Predicted execution time (float)</li>
    </ul>
    """

if __name__ == '__main__':
    app.run(debug=True)
