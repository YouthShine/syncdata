package  data.yunsom.com.kmeans;

public class KMeanVo {
	private double[] centroids;
	private double[][] clusterAssment;
	
	public KMeanVo() {
	}

	public double[] getCentroids() {
		return centroids;
	}

	public void setCentroids(double[] centroids) {
		this.centroids = centroids;
	}

	public KMeanVo(double[] centroids, double[][] clusterAssment) {
		super();
		this.centroids = centroids;
		this.clusterAssment = clusterAssment;
	}

	public double[][] getClusterAssment() {
		return clusterAssment;
	}

	public void setClusterAssment(double[][] clusterAssment) {
		this.clusterAssment = clusterAssment;
	}
	
}
