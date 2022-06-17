import java.util.Scanner;
public class Factory
{
	public static void main(String args[]) {
                Scanner userInput= new Scanner(System.in);
                int memorySize, frameSize, numberOfFrames;
                
                //prompt user to enter memory size
                System.out.println("Please enter memory size");
                memorySize= userInput.nextInt();
                //prompt user to enter frame size
                System.out.println("Please enter frame size.");
                frameSize = userInput.nextInt();
                
                //calculate number of frames
                numberOfFrames= (int)memorySize/ frameSize;
                
		PhysicalMemory physicalMemory = new PhysicalMemory(numberOfFrames);
		Thread[] threadArray = new Thread[5];

		for(int i=0; i<5 ;i++)
	     		threadArray[i] = new Thread(new MemoryThread(physicalMemory, i));
		for(int i=0; i<5; i++)
			threadArray[i].start();
	}
}
