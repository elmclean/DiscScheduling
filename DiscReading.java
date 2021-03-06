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
	public static int diskSize;
	public static char direction;

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		DiscReading disc = new DiscReading(); 

		System.out.print("Enter disk size: ");
		diskSize = in.nextInt();

		System.out.print("Enter current cylinder location: ");
		head = in.nextInt();

		while(head > diskSize || head <= 0) {
			System.out.print("Location out of disk size range, enter current cylinder location: ");
			head = in.nextInt();
		}

		System.out.print("Read disk to the right or left (r/l)? ");
		direction = in.next().charAt(0);

		while(direction != 'r' && direction != 'l') {
			System.out.print("Wrong input, read disk to the right or left (r/l)? ");
			direction = in.next().charAt(0);
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

	// First Come First Serve algorithm
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
		averageSeek = seekTime / (queue.length + 1);
		System.out.println("FCFS average seek time: " + averageSeek + "\n");
	}

	// Shortest Seek Time First algorithm
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
		averageSeek = seekTime / (queue.length + 1);
		System.out.println("SSTF average seek time: " + averageSeek + "\n");
	}

	// LOOK algorithm
	public void lookAlgorithm() {
		seekTime = 0;
		int[] tmpQueue = new int[queue.length + 1];
		int index = -1;
		int start = head;

		tmpQueue[queue.length] = start;

		for(int i = 0; i < queue.length; i++) {
			tmpQueue[i] = queue[i];
		}

		sortQueue(tmpQueue);

		for(int i = 0; i < tmpQueue.length; i++) {
			if(start == tmpQueue[i]) { 
				index = i;
			}
		}

		if(direction == 'l') {
			// reading to the left (descending)
			for(int i = index; i > 0; i--) {
				seekTime = seekTime + Math.abs(tmpQueue[i] - tmpQueue[i-1]);
			}

			// change direction
			seekTime = seekTime + Math.abs(tmpQueue[index+1] - tmpQueue[0]);

			// reading to the right (ascending)
			for(int i = index+1; i < tmpQueue.length-1; i++) {
				seekTime = seekTime + Math.abs(tmpQueue[i+1] - tmpQueue[i]);
			}
		} else if (direction == 'r') {
			// reading to the right (ascending)
			for(int i = index; i < tmpQueue.length-1; i++) {
				seekTime = seekTime + Math.abs(tmpQueue[i] - tmpQueue[i+1]);
			}

			// change direction
			seekTime = seekTime + Math.abs(tmpQueue[tmpQueue.length-1] - tmpQueue[index-1]);

			// reading to the left (descending)
			for(int i = index-1; i > 0; i--) {
				seekTime = seekTime + Math.abs(tmpQueue[i] - tmpQueue[i-1]);
			}
		}

		System.out.println("---LOOK ALGORITHM---");
		System.out.println("LOOK total seek time: " + seekTime);
		averageSeek = seekTime / tmpQueue.length;
		System.out.println("LOOK average seek time: " + averageSeek + "\n");
	}

	// Circular LOOK algorithm 
	public void clookAlgorithm() {
		seekTime = 0;
		int[] tmpQueue = new int[queue.length + 1];
		int index = -1;
		int start = head;

		tmpQueue[queue.length] = start;

		for(int i = 0; i < queue.length; i++) {
			tmpQueue[i] = queue[i];
		}

		sortQueue(tmpQueue);

		for(int i = 0; i < tmpQueue.length; i++) {
			if(start == tmpQueue[i]) { 
				index = i;
			}
		}

		if(direction == 'l') {
			// reading to the left (descending)
			for(int i = index; i > 0; i--) {
				seekTime = seekTime + Math.abs(tmpQueue[i] - tmpQueue[i-1]);
			}

			// change direction
			// doesn't actually count this: seekTime = seekTime + Math.abs(tmpQueue[0] - tmpQueue[tmpQueue.length-1]);

			// descending again from other side
			for(int i = tmpQueue.length - 1; i > index + 1; i--) {
				seekTime = seekTime + Math.abs(tmpQueue[i] - tmpQueue[i-1]);
			}
		} else if(direction == 'r') {
			// reading to the right (ascending)
			for (int i = index; i < tmpQueue.length-1; i++) {
				seekTime = seekTime + Math.abs(tmpQueue[i] - tmpQueue[i+1]);
			}

			// change direction
			// doesn't actually count this: seekTime = seekTime + Math.abs(tmpQueue[tmpQueue.length-1] - tmpQueue[0]);

			// ascending again from the other side
			for(int i = 0; i < index-1; i++) {
				seekTime = seekTime + Math.abs(tmpQueue[i] - tmpQueue[i+1]);
			}
		}

		System.out.println("---C-LOOK ALGORITHM---");
		System.out.println("C-LOOK total seek time: " + seekTime);
		averageSeek = seekTime / tmpQueue.length;
		System.out.println("C-LOOK average seek time: " + averageSeek + "\n");
	}

	// SCAN aka Elevator algorithm
	public void scanAlgorithm() {
		seekTime = 0;
		int[] tmpQueue = new int[queue.length + 1];
		int index = -1;
		int start = head;

		tmpQueue[queue.length] = start;

		for(int i = 0; i < queue.length; i++) {
			tmpQueue[i] = queue[i];
		}

		sortQueue(tmpQueue);

		for(int i = 0; i < tmpQueue.length; i++) {
			if(start == tmpQueue[i]) { 
				index = i;
			}
		}

		if(direction == 'l') {
			// reading to the left (descending)
			for(int i = index; i > 0; i--) {
				seekTime = seekTime + Math.abs(tmpQueue[i] - tmpQueue[i-1]);
			}
			
			// reading to the left (descending): 0
			seekTime = seekTime + Math.abs(tmpQueue[0] - 0);
			seekTime = seekTime + Math.abs(tmpQueue[index+1] - 0);

			for(int i = index+1; i < tmpQueue.length - 1; i++) {
				seekTime = seekTime + Math.abs(tmpQueue[i+1] - tmpQueue[i]);
			}
		} else if(direction == 'r') {
			// reading to the right (ascending)
			for(int i = index; i < tmpQueue.length-1; i++) {
				seekTime = seekTime + Math.abs(tmpQueue[i] - tmpQueue[i+1]);
			}

			// reading to the right (asending): diskSize-1
			seekTime = seekTime + Math.abs(tmpQueue[tmpQueue.length-1] - (diskSize-1));
			seekTime = seekTime + Math.abs(0 - tmpQueue[0]);

			// reading to the the right (ascending)
			for(int i = 0; i < index-1; i++) {
				seekTime = seekTime + Math.abs(tmpQueue[i] - tmpQueue[i+1]);
			}
		}
		
		System.out.println("---SCAN ALGORITHM---");
		System.out.println("SCAN total seek time: " + seekTime);
		averageSeek = seekTime / tmpQueue.length;
		System.out.println("SCAN average seek time: " + averageSeek + "\n");
	}

	// Circular SCAN algorithm
	public void cscanAlgorithm() {
		seekTime = 0;
		int[] tmpQueue = new int[queue.length + 2];
		int index = -1;
		int start = head;

		tmpQueue[tmpQueue.length-1] = start;
		tmpQueue[tmpQueue.length-2] = diskSize - 1;

		for(int i = 0; i < queue.length; i++) {
			tmpQueue[i] = queue[i];
		}

		sortQueue(tmpQueue);

		for(int i = 0; i < tmpQueue.length; i++) {
			if(start == tmpQueue[i]) { 
				index = i;
			}
		}

		if(direction == 'l') {
			// reading to the left (descending)
			for(int i = index; i > 0; i--) {
				seekTime = seekTime + Math.abs(tmpQueue[i] - tmpQueue[i-1]);
			}

			// reading to the left (descending) to 0
			seekTime = seekTime + Math.abs(tmpQueue[0] - 0);

			// reading from the left (descending) from diskSize-1
			for(int i = tmpQueue.length-1; i > index+1; i--) {
				seekTime = seekTime + Math.abs(tmpQueue[i] - tmpQueue[i-1]);
			}
		} else if(direction == 'r') {
			// reading to the right (ascending)
			for(int i = index; i < tmpQueue.length-1; i++) {
				seekTime = seekTime + Math.abs(tmpQueue[i] - tmpQueue[i+1]);
			}

			// reading to the right (ascending) from 0
			seekTime = seekTime + Math.abs(0 - tmpQueue[0]);

			// reading to the right (ascending)
			for(int i = 0; i < index-1; i++) {
				seekTime = seekTime + Math.abs(tmpQueue[i] - tmpQueue[i+1]);
			}
		}

		System.out.println("---C-SCAN ALGORITHM---");
		System.out.println("C-SCAN total seek time: " + seekTime);
		averageSeek = seekTime / (tmpQueue.length-1);
		System.out.println("C-SCAN average seek time: " + averageSeek + "\n");
	}

	// other --------------------------------------------------------------------
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

	public void sortQueue(int[] tmpQueue) {
		int temp;

		for(int i = 0; i < tmpQueue.length; i++) {
			for(int j = i; j < tmpQueue.length; j++) {
				if(tmpQueue[i] > tmpQueue[j]) {
					temp = tmpQueue[i];
					tmpQueue[i] = tmpQueue[j];
					tmpQueue[j] = temp;
				}
			}
		}
	}
}