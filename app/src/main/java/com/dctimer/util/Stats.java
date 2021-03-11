package com.dctimer.util;

import android.util.Log;

import static com.dctimer.APP.sortType;
import static com.dctimer.APP.timerAccuracy;
import static com.dctimer.util.StringUtils.timeToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import com.dctimer.APP;
import com.dctimer.model.Result;

public class Stats {
    public int[] avg1 = new int[24];
    public int[] avg2 = new int[24];
    public int[] bestAvg = {0, 0};
    public int[] bestAvgIdx = {-1, -1};
    public int mean = -1;
    public int sd;
    public int minIdx, maxIdx;
    public int solved;
    public int[] sortIdx = new int[24];

    public int[] mpMin = {-1, -1, -1, -1, -1, -1}, mpMax = {-1, -1, -1, -1, -1, -1};
    public int[] mpMean = new int[6];
    private Result result;

    public Stats(Result result) {
        this.result = result;
    }

    public void calcAvg() {
        if (avg1.length <= result.length()) {
            avg1 = new int[result.length() * 3 / 2];
            avg2 = new int[result.length() * 3 / 2];
        }
        bestAvg[0] = bestAvg[1] = Integer.MAX_VALUE;
        bestAvgIdx[0] = bestAvgIdx[1] = -1;
        long t = System.currentTimeMillis();
        for (int i = 0; i< result.length(); i++) {
            if (APP.avg1Type == 0) {
                avg1[i] = averageOf(APP.avg1len, i, 0);
            } else avg1[i] = meanOf(APP.avg1len, i, 0);
            if (APP.avg2Type == 0) {
                avg2[i] = averageOf(APP.avg2len, i, 1);
            } else avg2[i] = meanOf(APP.avg2len, i, 1);
        }
        t = System.currentTimeMillis() - t;
        Log.w("dct", "avg " + t);
    }

    public int meanOf(int n, int i, int midx) {
        if (i < n - 1) return -2;
        double sum = 0;
        for (int j = i - n + 1; j <= i; j++)
            if (result.isDnf(j)) {
                //if (i < n) bestAvg[midx] = Integer.MAX_VALUE;
                return -1;
            } else {
                int time = result.getTime(j);
                if (timerAccuracy == 0) time /= 10;
                sum += time;
            }
        int mean = (int) (sum / n + 0.5);
        if (timerAccuracy == 0) mean *= 10;
        //if (i == n - 1) { bestAvg[midx] = mean; bestAvgIdx[midx] = i; }
        if (midx >= 0 && mean <= bestAvg[midx]) {
            bestAvg[midx] = mean;
            bestAvgIdx[midx] = i;
        }
        return mean;
    }

    public int averageOf(int n, int i, int midx) {
        if (i < n - 1) return -2;
        int nDnf = 0, avg;
        int trim = (int) Math.ceil(n / 20.0);
        double sum = 0;

        if (n <= 20) {
            int max = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;
            for (int j = i - n + 1; j <= i; j++)
                if (result.isDnf(j)) {
                    nDnf++;
                    if (nDnf > trim)  return -1;
                } else {
                    int time = result.getTime(j);
                    if (time > max) max = time;
                    if (time < min) min = time;
                    if (timerAccuracy == 0) time /= 10;
                    sum += time;
                }
            if (nDnf != 0) max = 0;
            if (timerAccuracy == 0) {
                min /= 10;
                max /= 10;
            }
            sum -= min + max;
            avg = (int) (sum / (n - 2) + 0.5);
        } else {
            int[] max = new int[trim];
            int[] min = new int[trim];
            int size = 0;
            for (int j = i - n + 1; j <= i; j++) {
                if (result.isDnf(j)) {
                    nDnf++;
                    if (nDnf > trim) return -1;
                    if (size < trim) {
                        Utils.addMinQueue(min, Integer.MAX_VALUE, size);
                        Utils.addMaxQueue(max, Integer.MAX_VALUE, size++);
                    } else if (max[0] < Integer.MAX_VALUE) {
                        Utils.pollMax(max, trim);
                        Utils.addMaxQueue(max, Integer.MAX_VALUE, trim-1);
                    }
                } else {
                    int time = result.getTime(j);
                    if (timerAccuracy == 0) time /= 10;
                    sum += time;
                    if (size < trim) {
                        Utils.addMinQueue(min, time, size);
                        Utils.addMaxQueue(max, time, size++);
                    } else {
                        if (time < min[0]) {
                            Utils.pollMin(min, trim);
                            Utils.addMinQueue(min, time, trim-1);
                        }
                        if (time > max[0]) {
                            Utils.pollMax(max, trim);
                            Utils.addMaxQueue(max, time, trim-1);
                        }
                    }
                }
            }
            for (int j = 0; j < trim; j++) {
                sum -= min[j];
                if (max[j] != Integer.MAX_VALUE)
                    sum -= max[j];
            }
            avg = (int) (sum / (n - 2 * trim) + 0.5);
        }
        if (timerAccuracy == 0) avg *= 10;
        if (midx >= 0 && avg <= bestAvg[midx]) {
            bestAvg[midx] = avg;
            bestAvgIdx[midx] = i;
        }
        return avg;
    }

    public String sessionMean() {
        double sum = 0, sum2 = 0;
        maxIdx = minIdx = mean = -1;
        solved = result.length();
        if (solved == 0) return "0/0): N/A (N/A)";
        for (int i = 0; i < result.length(); i++) {
            if (result.isDnf(i)) solved--;
            else {
                int time = result.getTime(i);
                if (maxIdx == -1) maxIdx = i;
                else if (time > result.getTime(maxIdx)) maxIdx = i;
                if (minIdx == -1) minIdx = i;
                else if (time <= result.getTime(minIdx)) minIdx = i;
                if (timerAccuracy == 0) time /= 10;
                sum += time;
                sum2 += (double) time * time;
            }
        }
        if (solved == 0) return "0/" + result.length() + "): N/A (N/A)";
        mean = (int) (sum / solved + 0.5);
        if (timerAccuracy == 0) mean *= 10;
        sd = (int) (Math.sqrt((sum2 - sum * sum / solved) / solved) + 0.5);
        return solved + "/" + result.length() + "): " + timeToString(mean) + " (" + getSD(sd) + ")";
    }

    public String sessionAvg() {
        int n = result.length();
        if (n < 3) return "N/A";
        int[] data = new int[n];
        int count = 0;
        int trim = (int) Math.ceil(n / 20.0);
        for (int i = 0; i< result.length(); i++) {
            if (result.isDnf(i)) {
                n--;
                if (n < result.length() - trim) return "DNF";
            } else data[count++] = result.getTime(i);
        }
        double sum = 0, sum2 = 0;
        Arrays.sort(data, 0, count);
        for (int j = trim; j < result.length() - trim; j++) {
            if (timerAccuracy == 0) data[j] /= 10;
            sum += data[j];
            sum2 += (double) data[j] * data[j];
        }
        int num = result.length() - 2 * trim;
        int savg = (int) (sum / num + 0.5);
        if (timerAccuracy == 0) savg *= 10;
        int ssd = (int) (Math.sqrt((sum2 - sum * sum / num) / num) + 0.5);
        return timeToString(savg) + " (σ = " + getSD(ssd) + ")";
    }

    public String[] getAvgDetail(int nSolves, int idx, ArrayList<Integer> ary) {
        int avg = 0, sd = -1;
        int trim = (int) Math.ceil(nSolves / 20.0);
        int max, min;
        ArrayList<Integer> dnfIdx = new ArrayList<>();
        for (int j = idx - nSolves + 1; j <= idx; j++)
            if (result.isDnf(j))
                dnfIdx.add(j);
        int dnf = dnfIdx.size();
        long[] data = new long[nSolves - dnf];
        int len = 0;
        for (int j = idx - nSolves + 1; j <= idx; j++)
            if (!result.isDnf(j))
                data[len++] = (long) result.getTime(j) << 32 | j;
        Arrays.sort(data);
        if (nSolves - dnf >= trim) {
            for (int j = 0; j < trim; j++) ary.add((int) data[j]);
        } else {
            for (int j = 0; j < data.length; j++) ary.add((int) data[j]);
            for (int j = 0; j < trim - nSolves + dnf; j++) ary.add(dnfIdx.get(j));
        }
        boolean m = dnf > trim;
        min = ary.get(0);
        if (m) {
            for (int j = dnf - trim; j < dnf; j++) ary.add(dnfIdx.get(j));
        } else {
            for (int j = nSolves - trim; j < nSolves - dnf; j++) ary.add((int) data[j]);
            for (int j = 0; j < dnf; j++) ary.add(dnfIdx.get(j));
            double sum = 0, sum2 = 0;
            for (int j = trim; j < nSolves - trim; j++) {
                data[j] >>= 32;
                if (timerAccuracy == 0) data[j] /= 10;
                sum += data[j];
                sum2 += (double) data[j] * data[j];
            }
            int num = nSolves - trim * 2;
            avg = (int) (sum / num + 0.5);
            //Log.w("dct", sum+"/"+num+"="+cavg);
            sd = (int) (Math.sqrt((sum2 - sum * sum / num) / num) + 0.5);
            if (timerAccuracy == 0) avg *= 10;
        }
        max = ary.get(ary.size() - 1);
        return new String[] {m ? "DNF" : timeToString(avg), getSD(sd),
                result.getTimeAt(min, false), result.getTimeAt(max, false)};
    }

    public String[] getMeanDetail(int n, int i) {
        int max, min, dnf = 0;
        int cavg = 0, csdv = -1;
        double sum = 0, sum2 = 0;
        max = min = i - n + 1;
        boolean m = false;
        for (int j = i - n + 1; j <= i; j++) {
            if (!result.isDnf(j) && !m) { min = j; m = true; }
            if (result.isDnf(j)) { max = j; dnf++; }
        }
        m = dnf > 0;
        if (!m) {
            for (int j = i - n + 1; j <= i; j++) {
                int time = result.getTime(j);
                if (time > result.getTime(max)) max = j;
                if (time <= result.getTime(min)) min = j;
                if (timerAccuracy == 0) time /= 10;
                sum += time;
                sum2 += (double) time * time;
            }
            cavg = (int) (sum / n+0.5);
            csdv = (int) (Math.sqrt((sum2 - sum * sum / n) / n) + 0.5);
        }
        if (timerAccuracy == 0) cavg *= 10;
        return new String[] {m ? "DNF" : timeToString(cavg), getSD(csdv),
                result.getTimeAt(min, false), result.getTimeAt(max, false)};
    }

    public void calcMpMean() {
        for (int i = 0; i <= APP.multiPhase; i++) {
            if (result.length() == 0) mpMean[i] = 0;
            else mpMean[i] = mpMean(i);
        }
    }

    private int mpMean(int p) {
        long sum = 0;
        int n = 0;
        int max = 0, min = Integer.MAX_VALUE;
        mpMin[p] = mpMax[p] = -1;
        if (n == result.length()) return 0;
        for (int i = 0; i < result.length(); i++) {
            int time = result.getMulTime(p, i);
            if (time != 0) {
                if (timerAccuracy == 0) time /= 10;
                if (time > max) {
                    max = time;
                    mpMax[p] = i;
                }
                if (time <= min) {
                    min = time;
                    mpMin[p] = i;
                }
                sum += time;
                n++;
            }
        }
        if (n == 0) return 0;
        int m = (int) (sum / n + 0.5);
        if (timerAccuracy == 0) m *= 10;
        return m;
    }

    public String getSD(int i) {
        if (i < 0) return "N/A";
        if (timerAccuracy == 1) i = (i + 5) / 10;
        return String.format(Locale.getDefault(), "%.2f", i / 100f);
    }

    public void sortResult() {
        int len = result.length();
        if (sortIdx.length <= len) {
            sortIdx = new int[len * 3 / 2];
        }
        for (int i = 0; i < len; i++) sortIdx[i] = i;
        int type = (sortType - 1) / 2;
        if (type == 0) quickSort(result.getResult(), true, 0, len - 1);
        else if (type == 1) quickSort(avg1, false, 0, len - 1);
        else if (type == 2) quickSort(avg2, false, 0, len - 1);
        if (sortType % 2 == 0) {
            int[] s = new int[sortIdx.length];
            for (int i = 0; i < len; i++) {
                s[len - 1 - i] = sortIdx[i];
            }
            sortIdx = s;
        }
    }

    private void quickSort(int[] arr, boolean penalty, int low, int high) {
        int start = low;
        int end = high;
        int key = getData(arr, sortIdx[low], penalty);//arr[sortIdx[low]];
        while (end > start) {
            while (end > start && getData(arr, sortIdx[end], penalty) >= key) end--;
            if (getData(arr, sortIdx[end], penalty) <= key) {
                int temp = sortIdx[end];
                sortIdx[end] = sortIdx[start];
                sortIdx[start] = temp;
            }
            while (end > start && getData(arr, sortIdx[start], penalty) <= key) start++;
            if (getData(arr, sortIdx[start], penalty) >= key) {
                int temp = sortIdx[start];
                sortIdx[start] = sortIdx[end];
                sortIdx[end] = temp;
            }
        }
        if (start > low) quickSort(arr, penalty, low, start - 1);
        if (end < high) quickSort(arr, penalty, end + 1, high);
    }

    private int getData(int[] arr, int pos, boolean penalty) {
        if (!penalty) {
            if (arr[pos] == -1) return Integer.MAX_VALUE;
            return arr[pos];
        }
        if (result.isDnf(pos))
            return Integer.MAX_VALUE;
        return arr[pos] + (result.getPenalty(pos) == 1 ? 2000 : 0);
    }
}
