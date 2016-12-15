package  data.yunsom.com.kmeans;

import java.util.ArrayList;
import java.util.List;

public class PythonUtil {
	
	public static double avg(double[] ds) {
		double sum = 0D;
		for (int i = 0; i < ds.length; i++) {
			sum += ds[i];
		}
		return sum/ds.length;
	}
	
	public static double[] copy(double[] dataSet, double[][] clusterAssment, double value) {
		List<Double> ptsInCurrCluster = new ArrayList<Double>();
		for (int j = 0; j < clusterAssment.length; j ++) {
			if (clusterAssment[j][0] == value) {
				ptsInCurrCluster.add(dataSet[j]);
			}
		}
		
		double[] ds = new double[ptsInCurrCluster.size()];
		for (int j = 0; j < ptsInCurrCluster.size(); j ++) {
			ds[j] = ptsInCurrCluster.get(j);
		}
		return ds;
	}
	
	public static double[] copyNotEqual(double[] dataSet, double[][] clusterAssment, double value) {
		List<Double> ptsInCurrCluster = new ArrayList<Double>();
		for (int j = 0; j < clusterAssment.length; j ++) {
			if (clusterAssment[j][0] != value) {
				ptsInCurrCluster.add(dataSet[j]);
			}
		}
		
		double[] ds = new double[ptsInCurrCluster.size()];
		for (int j = 0; j < ptsInCurrCluster.size(); j ++) {
			ds[j] = ptsInCurrCluster.get(j);
		}
		return ds;
	}
	
	public static int[] martrixAdd(int[] m1, int[]m2) {
		for (int i = 0; i < m1.length; i ++) {
			m1[i] += m2[i];
		}
		return m1;
	}
	
	public static double[] matrixDiv(int[] m1, double m2) {
		double[] ds = new double[m1.length];
		for (int i = 0; i < m1.length; i++) {
			ds[i] = (double)m1[i]/(double)m2;
		}
		return ds;
	}
	
	public static int[] zeros(int num) {
		
		return new int[num];
	}
	
	public static double sum(double[] trainCategory) {
		double sum = 0;
		for (int i = 0; i < trainCategory.length; i++) {
			sum += trainCategory[i];
		}
		return sum;
	}
	
	public static int sum(int[] trainCategory) {
		int sum = 0;
		for (int i = 0; i < trainCategory.length; i++) {
			sum += trainCategory[i];
		}
		return sum;
	}
}
