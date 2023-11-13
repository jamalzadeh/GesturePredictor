package org.example;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class GesturePredictor {
    // Instance variables
    private String status;
    private float alpha;
    private float beta;
    private float gamma;
    private float width_coef = (float) 1.0;
    private float height_coef = (float) 1.0;
    private float duration_coef = (float) 1.0;
    private boolean saturation;
    // Attention Saturating correction factors
    private float saturation_width_correction = (float) 2.33/ (float) 2.49;
    private float saturation_height_correction= (float) 2.40/ (float)2.64;
    private float saturation_duration_correctin= (float) 0.96/ (float) 1.05;

    // reference alpha, beta, and gammas
    // Sitting
    private float sitting_width_correction = (float) 1.0;
    private float sitting_height_correction= (float) 1.0;
    private float sitting_duration_correctin= (float) 1.0;
    // width
    private float standing_width_correction = (float) 3.06 / (float) 3.01;
    private float walking_width_correction = (float) 3.15 / (float) 3.01;
    private float jogging_width_correction = (float) 3.44 / (float) 3.01;
    //height
    private float standing_height_correction = (float) 3.70 / (float) 3.71;
    private float walking_height_correction = (float) 3.83 / (float) 3.71;
    private float jogging_height_correction = (float) 3.87 / (float) 3.71;

    //duration
    private float standing_duration_correction = (float) 1.0;
    private float walking_duration_correction = (float) 1.0;
    private float jogging_duration_correction = (float) 1.0;
    List<DataPoint> dataPoints = new ArrayList<>();

    public GesturePredictor(float alpha, float beta, float gamma, boolean saturation){
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        this.saturation = saturation;
        dataPoints.add(new DataPoint(2.84, 2.80, 2.75, "Sitting"));
        dataPoints.add(new DataPoint(4.91, 3.0, 4.0, "Standing"));
        dataPoints.add(new DataPoint(9.19, 4.0, 5.0, "Walking"));
        dataPoints.add(new DataPoint(14.34, 5.0, 6.0, "Jogging"));
        // Create a KNN classifier
        KNN knn = new KNN(dataPoints);

        // Create a new data point to classify
        DataPoint newDataPoint = new DataPoint(alpha, beta, gamma, "");
        // Classify the new data point
        this.status= knn.classify(newDataPoint, 1);
        this.Saturation_correction();
        this.Status_correction();

    }
    public float getWidth_coef(){
        return this.width_coef;
    }
    public float getHeight_coef(){
        return this.height_coef;
    }
    public float getDuration_coef(){
        return getDuration_coef();
    }
    private void Status_correction(){
        switch (this.status){
            case "Sitting":
                break;
            case "Standing":
                this.width_coef = this.width_coef * standing_width_correction;
                this.height_coef = this.height_coef * standing_height_correction;
                this.duration_coef = this.duration_coef * standing_duration_correction;
            case "Walking":
                this.width_coef = this.width_coef * walking_width_correction;
                this.height_coef = this.height_coef * walking_height_correction;
                this.duration_coef = this.duration_coef * walking_duration_correction;
            case "Jogging":
                this.width_coef = this.width_coef * jogging_width_correction;
                this.height_coef = this.height_coef * jogging_height_correction;
                this.duration_coef = this.duration_coef * jogging_duration_correction;
        }

    }
    private void Saturation_correction(){
        this.width_coef = this.width_coef * saturation_width_correction;
        this.height_coef = this.height_coef * saturation_height_correction;
        this.duration_coef = this.duration_coef * saturation_duration_correctin;
    }
    private void find_nearest_neighbor( float alpha, float beta, float gamma){

    }
    public boolean isSaturation() {
        return saturation;
    }

    public void setSaturation(boolean saturation) {
        this.saturation = saturation;
    }



    class DataPoint {
        double x;
        double y;
        double z;
        String label;

        public DataPoint(double x, double y, double z, String label) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.label = label;
        }
    }

    public class KNN {

        private List<DataPoint> dataPoints;

        public KNN(List<DataPoint> dataPoints) {
            this.dataPoints = dataPoints;
        }

        // KNN algorithm
        public String classify(DataPoint newDataPoint, int k) {
            List<DataPoint> neighbors = getKNearestNeighbors(newDataPoint, k);
            return getMajorityVote(neighbors);
        }

        // Calculate Euclidean distance between two data points
        private double calculateDistance(DataPoint p1, DataPoint p2) {
            return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2) + Math.pow(p1.z - p2.z, 2));
        }

        // Get k-nearest neighbors for a given data point
        private List<DataPoint> getKNearestNeighbors(DataPoint newDataPoint, int k) {
            List<DataPoint> distances = new ArrayList<>();

            for (DataPoint dataPoint : dataPoints) {
                double distance = calculateDistance(newDataPoint, dataPoint);
                distances.add(new DataPoint(distance, 0, 0, dataPoint.label));
            }

            Collections.sort(distances, (dp1, dp2) -> Double.compare(dp1.x, dp2.x));

            return distances.subList(0, k);
        }

        // Get the majority vote from a list of neighbors
        private String getMajorityVote(List<DataPoint> neighbors) {
            int countClassA = 0;
            int countClassB = 0;

            for (DataPoint neighbor : neighbors) {
                if (neighbor.label.equals("ClassA")) {
                    countClassA++;
                } else if (neighbor.label.equals("ClassB")) {
                    countClassB++;
                }
            }

            return (countClassA > countClassB) ? "ClassA" : "ClassB";
        }

    }

}
