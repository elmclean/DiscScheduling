import java.io.*;
import java.util.*;
import javax.swing.*;

public class DiscReading
{
	public static int head;
	public static int[] queue;
	public static int distance;
	public static int seekTime;
	public static float averageSeek;
	public static final int DISK_SIZE = 5000;

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		DiscReading disc = new DiscReading(); 

		System.out.print("Enter current cylinder location (0 - 4,999): ");
		head = in.nextInt();

		while(head > 4999 || head < 0) {
			System.out.print("Location out of range, enter current cylinder location (0 - 4,999): ");
			head = in.nextInt();
		}

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("queue.txt")))) {
        	String line = reader.readLine();
        	if(line != "") {
        		String[] tmpQueue = line.split(",");
        		queue = new int[tmpQueue.length];

        		for(int i = 0; i < tmpQueue.length; i++) {
        			queue[i] = Integer.parseInt(tmpQueue[i]);
        		}

        		disc.fcfsAlgorithm();
        		disc.sstfAlorithm();
        		disc.lookAlgorithm();
        		disc.clookAlgorithm();
        		disc.scanAlgorithm();
        		disc.cscanAlgorithm();
        	} else {
        		System.out.println("ERROR - empty file");
        	}
        } catch (IOException e) {
            System.out.println("ERROR - cannot open file");
        }
	}

	// First Come First Serve
	public void fcfsAlgorithm() {
		seekTime = 0;
		distance = Math.abs(queue[0] - head);
		seekTime = seekTime + distance;

		// System.out.println("Move from " + head + " to " + queue[0] + " with distance " + distance);

		for(int i = 0; i < queue.length - 1; i++) {
			distance = Math.abs(queue[i+1] - queue[i]);
			seekTime = seekTime + distance;

			// System.out.println("Move from " + queue[i] + " to " + queue[i+1] + " with distance " + distance);
		}

		System.out.println("---FCFS ALGORITHM---");
		System.out.println("FCFS total seek time: " + seekTime);
		averageSeek = seekTime / (queue.length);
		System.out.println("FCFS average seek time: " + averageSeek + "\n");
	}

	// Shortest Seek Time First
	public void sstfAlorithm() {
		seekTime = 0;
		int[] tmpQueue = new int[queue.length];
		int index;
		int start = head;

		for(int i = 0; i < queue.length; i++) {
			tmpQueue[i] = queue[i];
		}

		for(int i = 0; i < queue.length; i++) {
			index = findShortestSeek(tmpQueue, start);
			seekTime = seekTime + Math.abs(start - tmpQueue[index]);
			start = tmpQueue[index];
			tmpQueue[index] = -1;
		}

		System.out.println("---SSTF ALGORITHM---");
		System.out.println("SSTF total seek time: " + seekTime);
		averageSeek = seekTime / queue.length;
		System.out.println("SSTF average seek time: " + averageSeek + "\n");
	}

	public void lookAlgorithm() {

	}

	public void clookAlgorithm() {
		seekTime = 0;
		int[] tmpQueue = new int[queue.length];
		int index;
		int start = head;
		boolean begin = false;

		for(int i = 0; i < queue.length; i++) {
			tmpQueue[i] = queue[i];
		}

		// distance = Math.abs(tmpQueue[index] - head);

		index = clookSort(tmpQueue, start);

		// System.out.println("Move from " + head + " to " + tmpQueue[index] + " with distance " + distance);

		for(int i = index-1; i > 0; i--) {
			distance = Math.abs(tmpQueue[i] - tmpQueue[i-1]);
			seekTime = seekTime + distance;

			// System.out.println("Move from " + queue[i] + " to " + queue[i+1] + " with distance " + distance);
		}

		System.out.println("---CLOOK ALGORITHM---");
		System.out.println("CLOOK total seek time: " + seekTime);
		averageSeek = seekTime / queue.length;
		System.out.println("CLOOK average seek time: " + averageSeek + "\n");
	}

	public void scanAlgorithm() {
		seekTime = 0;
		int[] tmpQueue = new int[queue.length];
		int index = -1;
		int start = head;

		for(int i = 0; i < queue.length; i++) {
			tmpQueue[i] = queue[i];
		}

		sortQueue(tmpQueue);

		for(int i=0; i < queue.length; i++) {
			if(start > tmpQueue[i]) { 
				index = i; 
				break;  
			}
		}
		for(int i = index; i >= 0; i--) {
			System.out.print(" --> " + tmpQueue[i]);
		}
		System.out.print("0 --> ");
		for(int i = index+1; i < queue.length; i++) {
			System.out.print("--> " + tmpQueue[i]);
		}
		
		// seekTime = start + max;
		System.out.println("---SCAN ALGORITHM---");
		// System.out.println("\nmovement of total cylinders: " + seekTime);

		// System.out.println("---CLOOK ALGORITHM---");
		// System.out.println("CLOOK total seek time: " + seekTime);
		// averageSeek = seekTime / queue.length;
		// System.out.println("CLOOK average seek time: " + averageSeek + "\n");
	}

	public void cscanAlgorithm() {

	}

	// --------------------------------------------------------------------
	public int findShortestSeek(int[] tmpQueue, int start) {
		int min = 999999; // large initial value to check for min
		int index = -1;

		for(int i = 0; i < tmpQueue.length; i++) {
			if(tmpQueue[i] != -1) {
				distance = Math.abs(start - tmpQueue[i]);
				if(min > distance) {
					min = distance;
					index = i;
				}
			}
		}

		return index;
	}

	public int clookSort(int[] tmpQueue, int start) {
		int temp;

		for(int i = 0; i < queue.length - 1; i++) {
			for(int j = 0; j < queue.length - 1; j++) {
				if(tmpQueue[j] > tmpQueue[j+1]) {
					temp = tmpQueue[j];
					tmpQueue[j] = tmpQueue[j+1];
					tmpQueue[j+1] = temp;
				}
			}
		}

		for(int i = 0; i < queue.length; i++) {
			if(tmpQueue[i] > start) {
				return i;
			}
		}

		return queue.length;
	}

	public void sortQueue(int[] tmpQueue) {
		int temp;

		for(int i = 0; i < queue.length; i++) {
			for(int j = i; j < queue.length; j++) {
				if(tmpQueue[i] > tmpQueue[j]) {
					temp = tmpQueue[i];
					tmpQueue[i] = tmpQueue[j];
					tmpQueue[j] = temp;
				}
			}
		}
	}










}