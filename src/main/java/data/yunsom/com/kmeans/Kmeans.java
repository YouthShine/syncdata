package data.yunsom.com.kmeans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kmeans {
    
    public static void main(String[] args) {
        
        
        double[] dataSet = new double[]{10D,100D,110D,111D,115D,116D,150D,153D,154D,155D,150D};
        
        int order = Kmeans.exec(dataSet, 4, 97);
        System.out.println(order);
    }
    
    /**
     * 主调方法
     * @param dataSet 数据集
     * @param k 分点数量
     * @param sampleTimes 取样次数（如果k为3.聚会最好不能被2和3整除。依此类推）
     * @return
     */
    public static int exec(double[] dataSet, int k, int sampleTimes) {
        int[] orderSamples = new int[k];
        for (int i = 0; i < sampleTimes; i ++) {
            orderSamples[exec(dataSet, k)] ++ ;
        }
        
        int index = 0;
        int max = 0;
        for (int i = 0; i < k; i ++) {
            System.out.println("index:" + i +",Times:" + orderSamples[i]);
            if (orderSamples[i] > max) {
                max = orderSamples[i];
                index = i;
            }
        }
        return index;
    }
    
    public static int exec(double[] dataSet, int k) {
        Kmeans kmeans = new Kmeans();
        KMeanVo kMeanVo = kmeans.biKmeans(dataSet, k);
        for (double[] dd : kMeanVo.getClusterAssment()) {
//            System.out.println(dd[0] + "##" + dd[1]);
        }
        
        
        double[] orderdd = new double[k];
        for (int i = 0; i <kMeanVo.getClusterAssment().length; i ++) {
            double[] dd = kMeanVo.getClusterAssment()[i];
            orderdd[new Double(dd[0]).intValue()] = dataSet[i];
        }
        List<Double> ddList = new ArrayList<Double>();
        for (int i = 0; i < orderdd.length; i ++) {
            if (orderdd[i] == 0) {
                continue;
            }
            ddList.add(orderdd[i]);
        }
        Collections.sort(ddList);
        int[] rightOrder = new int[ddList.size()];
        for (int j = 0; j < ddList.size(); j ++) {
            for (int i = 0; i < orderdd.length; i ++) {
                if (orderdd[i] == ddList.get(j)) {
                    rightOrder[j] = i;
                }
            }
        }
        
//        for (int i = 0; i < rightOrder.length; i ++) {
//            System.out.println(i + "#rightOrder#" + rightOrder[i]);
//        }
        
        int lastOne = new Double(kMeanVo.getClusterAssment()[kMeanVo.getClusterAssment().length - 1][0]).intValue();
        int order = 0;
        for (int i = 0; i < rightOrder.length; i ++) {
            if (rightOrder[i] == lastOne) {
                order = i;
            }
        }
        if (rightOrder.length == 2 && order == 1) {
            double sum = 0D;
            for (int i = 0; i < dataSet.length; i ++) {
                sum += dataSet[i];
            }
            double avg = sum / dataSet.length;
            if (dataSet[dataSet.length - 1] > avg) {
                order ++;
            }
        }
        return order;
    }
    
    /**
     * 计算A点和B点的欧式距离
     * @param vecA
     * @param vecB
     * @return
     */
    public static double distEclud(double vecA, double vecB) {
        Double distEclud = null;
        distEclud = Math.sqrt(Math.pow(vecA - vecB, 2));
        return distEclud;
    }
    
    /**
     * 随机生成一个中心点。坐标。在dataSet的集合之内
     * @param dataSet
     * @param k
     */
    public double[] randCent(double[] dataSet, int k) {
        double[] randCenter = new double[k]; 
        double minY = dataSet[0];
        double maxY = dataSet[0];
        for (int i = 0; i < dataSet.length; i ++) {
            if (dataSet[i] > maxY) {
                maxY = dataSet[i];
            }
            if (dataSet[i] < minY) {
                minY = dataSet[i];
            }
        }
        
        double rangeY = maxY - minY;
        
        for (int i = 0; i < k; i ++) {
//          randCenter[i] = minY + rangeY*(i/k) + (rangeY + (1/(float)k)) * Math.random();
            randCenter[i] = minY + (rangeY * Math.random())*(i+1)/(float)k;
            
//          randCenter[i] = minY + (rangeY * Math.random());
//          randCenter[i] = minY + (rangeY + (1/(float)k)) * Math.random();
        }
        return randCenter;
    }
    int mm = 0;
//  double[][] cc = new double[][]{{144.52849798894113, 120.77393434885376}, {186.32261978787272, 163.3008950781162},{1, 1}};
    double[][] cc = new double[][]{{147.56402602992515, 147.60999150112772}, {180.26962713296666, 178.9918690969861},{1, 1}};
//  92.86829885272594,107.2490807565435
    
    public KMeanVo kMeans(double[] dataSet, int k) {
        int m = dataSet.length;
        double[][] clusterAssment = new double[m][2];
//      
        double[] centroids = randCent(dataSet, k);
//      double[] centroids = new double[]{  114.12162143 , 20.44107987};
//      double[] centroids = cc[mm++];
//      System.err.println(centroids[0] + "#$%" + centroids[1]);
        boolean clusterChanged = true;
        
        while (clusterChanged) {
            clusterChanged = false;
            for (int i = 0; i < m; i++) {
                double minDist = Double.MAX_VALUE;
                int minIndex = -1;
                
                for (int j = 0; j < k; j ++) {
                    double distJI = distEclud(centroids[j], dataSet[i]);
                    if (distJI < minDist) {
                        minDist = distJI;
                        minIndex = j;
                    }
                }
                if (clusterAssment[i][0] != minIndex) {
                    clusterChanged = true;
                }
                clusterAssment[i][0] = minIndex;
                clusterAssment[i][1] = minDist*minDist;
            }
            
            for (int cent = 0; cent < k; cent ++) {
                double sum = 0D;
                int count = 0;
                for (int j = 0; j < clusterAssment.length; j ++) {
                    if (clusterAssment[j][0] == cent) {
                        sum += dataSet[j];
                        count ++;
                    }
                }
                if (count != 0) {
                    centroids[cent] = sum/count;
                }
            }
        }
        return new KMeanVo(centroids, clusterAssment);
    }
    
    public KMeanVo biKmeans(double[] dataSet, int k) {
        int m = dataSet.length;
        double[][] clusterAssment = new double[m][2];
        double centroid0 = PythonUtil.avg(dataSet);
        
        List<Double> centList = new ArrayList<Double>();
        centList.add(centroid0);
        
        for (int i = 0; i < m; i ++) {
            clusterAssment[i][1] = Math.pow(distEclud(centroid0, dataSet[i]), 2);
        }

        int bestCentTopSplit = 0;
        while(centList.size() < k) {
            double lowestSSE = Double.MAX_VALUE;
            double[][] bestClustAss = new double[0][];
            double[] bestNewCents = null;
            for (int i = 0; i < centList.size(); i ++) {
                double[] ptsInCurrCluster = PythonUtil.copy(dataSet, clusterAssment, i);
//              for (int j = 0; j < clusterAssment.length; j++) {
//                  ptsInCurrCluster = PythonUtil.copy(dataSet, clusterAssment, i);
//              }
                KMeanVo kMeanVo = kMeans(ptsInCurrCluster, 2);
                double[] centroidMat = kMeanVo.getCentroids();
                double[][] splitClustAss = kMeanVo.getClusterAssment();
                
                double sseSplit = 0D;
                for (int j = 0; j < splitClustAss.length; j++) {
                    sseSplit += splitClustAss[j][1];
                }
                
                double[] ds = PythonUtil.copyNotEqual(dataSet, clusterAssment, i);
                double sseNotSplit = PythonUtil.sum(ds);
                
                if (sseSplit + sseNotSplit < lowestSSE) {
                    bestCentTopSplit = i;
                    bestNewCents = centroidMat;
                    bestClustAss = splitClustAss;
                    lowestSSE = sseSplit + sseNotSplit;
                }
            }
            
//          boolean isAllSame = true;
//          double aimVal = bestClustAss[0][0];
//          for (int i = 0; i < bestClustAss.length; i ++) {
//              if (aimVal != bestClustAss[i][0]) {
//                  isAllSame = false;
//                  break;
//              }
//          }
//          if (isAllSame) {
//              continue;
//          }
            
            //bestClustAss[nonzero(bestClustAss[:,0].A == 1)[0],0] = len(centList)
            for (int i = 0; i < bestClustAss.length; i ++) {
                if (bestClustAss[i][0] == 1) {
                    bestClustAss[i][0] = centList.size();
                }
            }
            //bestClustAss[nonzero(bestClustAss[:,0].A == 0)[0],0] = bestCentToSplit
            for (int i = 0; i < bestClustAss.length; i ++) {
                if (bestClustAss[i][0] == 0) {
                    bestClustAss[i][0] = bestCentTopSplit;
                }
            }
            centList.set(bestCentTopSplit, bestNewCents[0]);
                centList.add(bestNewCents[1]);
            
            int j = 0;
            for (int i = 0; i < clusterAssment.length; i++) {
                if (clusterAssment[i][0] == bestCentTopSplit) {
                    clusterAssment[i] = bestClustAss[j ++];
                }
            }
        }
        
        double[] centListArr = new double[centList.size()];
        for (int i = 0; i < centList.size(); i ++) {
            centListArr[i] = centList.get(i);
        }
        return new KMeanVo(centListArr, clusterAssment);
    }
}
