import java.util.Objects;
import java.util.concurrent.Semaphore;

public class PhysicalMemory
{

    private int freeFrameCount, numberOfFrames;
    private boolean[] memoryAllocationMap;
    private PageTableList tableList;
    Semaphore mutex;

    public PhysicalMemory(int numofFrames)
    {
        this.numberOfFrames = numofFrames;
        this.memoryAllocationMap = new boolean[numofFrames];
        this.freeFrameCount = numofFrames;
        this.tableList = new PageTableList();
        this.mutex = new Semaphore(1);

        for(int i = 0; i < memoryAllocationMap.length; i++)
        {
            memoryAllocationMap[i] = false;
        }
    }

    //method to request memory
    public boolean requestMemory(int id, int requestLength)
    {
        try
        {
            mutex.acquire();
        } catch (Exception exception)
        {
            exception.printStackTrace();
        }

        if(requestLength > freeFrameCount)
        {
            try
            {
                mutex.release();
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }

            return false;
        }

        PageTable pt = new PageTable(id, requestLength);

        int requestLengthCopy, idx = 0;
        requestLengthCopy = requestLength;

        while(requestLengthCopy > 0)
        {
            if(this.memoryAllocationMap[idx] == false)
            {
                this.memoryAllocationMap[idx] = true;
                pt.appendEntry(idx);
                requestLengthCopy--;
            }

            idx++;
        }


        tableList.addFirst(pt);

        this.freeFrameCount -= requestLength;

        try
        {
            mutex.release();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    //method to access the memory
    public int accessMemory(int id, int pageNumber)
    {
        PageTable pageTableResult = tableList.lookup(id);
        if(Objects.isNull(pageTableResult.lookupPageTable(pageNumber)))
        {
            return -1;
        }
        return pageTableResult.lookupPageTable(pageNumber);
    }

    //methos rto release the memory
    public void releaseMemory(int id)
    {
        try
        {
            mutex.acquire();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        PageTable pageTableResult = tableList.lookup(id);
        int frameNumber = 0;
        for(int i = 0; i < pageTableResult.getLength(); i++)
        {
            frameNumber = pageTableResult.lookupPageTable(i);
            this.memoryAllocationMap[frameNumber] = false;
            this.freeFrameCount += 1;
        }

        try
        {
            mutex.release();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
