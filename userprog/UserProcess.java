package nachos.userprog;

import nachos.machine.*;
import nachos.threads.*;
import nachos.userprog.*;

import java.io.EOFException;
import java.util.LinkedList;

/**
 * Encapsulates the state of a user process that is not contained in its
 * user thread (or threads). This includes its address translation state, a
 * file table, and information about the program being executed.
 *
 * <p>
 * This class is extended by other classes to support additional functionality
 * (such as additional syscalls).
 *
 * @see	nachos.vm.VMProcess
 * @see	nachos.network.NetProcess
 */
public class UserProcess {
	
	protected OpenFile[] fileTable;
	
	private Lock conditionLock;
	public int processID;
	public UserProcess parentProcess;
	private int exitStatus = -2;
	
    /**
     * Allocate a new process.
     */
    public UserProcess() {
		int numPhysPages = Machine.processor().getNumPhysPages();
		pageTable = new TranslationEntry[numPhysPages];
		for (int i=0; i<numPhysPages; i++)
		    pageTable[i] = new TranslationEntry(i,i, true,false,false,false);
		fileTable = new OpenFile[16];
		fileTable[0] = UserKernel.console.openForReading();
		fileTable[1] = UserKernel.console.openForWriting();
		conditionLock = new Lock();
    }
    
    /**
     * Allocate and return a new process of the correct class. The class name
     * is specified by the <tt>nachos.conf</tt> key
     * <tt>Kernel.processClassName</tt>.
     *
     * @return	a new process of the correct class.
     */
    public static UserProcess newUserProcess() {
	return (UserProcess)Lib.constructObject(Machine.getProcessClassName());
    }

    /**
     * Execute the specified program with the specified arguments. Attempts to
     * load the program, and then forks a thread to run it.
     *
     * @param	name	the name of the file containing the executable.
     * @param	args	the arguments to pass to the executable.
     * @return	<tt>true</tt> if the program was successfully executed.
     */
    public boolean execute(String name, String[] args) {
	if (!load(name, args))
	    return false;
	
	new UThread(this).setName(name).fork();

	return true;
    }

    /**
     * Save the state of this process in preparation for a context switch.
     * Called by <tt>UThread.saveState()</tt>.
     */
    public void saveState() {
    }

    /**
     * Restore the state of this process after a context switch. Called by
     * <tt>UThread.restoreState()</tt>.
     */
    public void restoreState() {
	Machine.processor().setPageTable(pageTable);
    }

    /**
     * Read a null-terminated string from this process's virtual memory. Read
     * at most <tt>maxLength + 1</tt> bytes from the specified address, search
     * for the null terminator, and convert it to a <tt>java.lang.String</tt>,
     * without including the null terminator. If no null terminator is found,
     * returns <tt>null</tt>.
     *
     * @param	vaddr	the starting virtual address of the null-terminated
     *			string.
     * @param	maxLength	the maximum number of characters in the string,
     *				not including the null terminator.
     * @return	the string read, or <tt>null</tt> if no null terminator was
     *		found.
     */
    public String readVirtualMemoryString(int vaddr, int maxLength) {
	Lib.assertTrue(maxLength >= 0);

	byte[] bytes = new byte[maxLength+1];

	int bytesRead = readVirtualMemory(vaddr, bytes);

	for (int length=0; length<bytesRead; length++) {
	    if (bytes[length] == 0)
		return new String(bytes, 0, length);
	}

	return null;
    }

    /**
     * Transfer data from this process's virtual memory to all of the specified
     * array. Same as <tt>readVirtualMemory(vaddr, data, 0, data.length)</tt>.
     *
     * @param	vaddr	the first byte of virtual memory to read.
     * @param	data	the array where the data will be stored.
     * @return	the number of bytes successfully transferred.
     */
    public int readVirtualMemory(int vaddr, byte[] data) {
	return readVirtualMemory(vaddr, data, 0, data.length);
    }

    /**
     * Transfer data from this process's virtual memory to the specified array.
     * This method handles address translation details. This method must
     * <i>not</i> destroy the current process if an error occurs, but instead
     * should return the number of bytes successfully copied (or zero if no
     * data could be copied).
     *
     * @param	vaddr	the first byte of virtual memory to read.
     * @param	data	the array where the data will be stored.
     * @param	offset	the first byte to write in the array.
     * @param	length	the number of bytes to transfer from virtual memory to
     *			the array.
     * @return	the number of bytes successfully transferred.
     */
    public int readVirtualMemory(int vaddr, byte[] data, int offset,
				 int length) {
	Lib.assertTrue(offset >= 0 && length >= 0 && offset+length <= data.length);

	byte[] memory = Machine.processor().getMemory();
	
	if (vaddr < 0 || offset < 0 || length < 0 || offset + length > data.length )
	    return 0;
	int bytesCopied = 0;
	int vpn = Processor.pageFromAddress(vaddr);
	int pageOffset = Processor.offsetFromAddress(vaddr);
	int bytesLeftToCopy  = length;
	int bufferOffset = offset;
   
    int paddr = -1;
    
    
    while(bytesLeftToCopy  > 0){
      
      for(int i = 0; i < pageTable.length; i++){
        if(pageTable[i].vpn == vpn && pageTable[i].valid){
          paddr = pageTable[i].ppn * pageSize;
          break;
        }
      }
      
      if(paddr < 0 || paddr >= memory.length){
        return bytesCopied;
      }
      int bytesToEndOfPage = pageSize - pageOffset;
      int bytesToCopy = Math.min(bytesLeftToCopy , bytesToEndOfPage);
      System.arraycopy(memory, paddr, data, bufferOffset, bytesToCopy);
      bytesCopied += bytesToCopy;
      bufferOffset += bytesToCopy;
      bytesLeftToCopy  -= bytesToCopy;
      vpn++;
      pageOffset = 0;
    }

		return bytesCopied;
    }

    /**
     * Transfer all data from the specified array to this process's virtual
     * memory.
     * Same as <tt>writeVirtualMemory(vaddr, data, 0, data.length)</tt>.
     *
     * @param	vaddr	the first byte of virtual memory to write.
     * @param	data	the array containing the data to transfer.
     * @return	the number of bytes successfully transferred.
     */
    public int writeVirtualMemory(int vaddr, byte[] data) {
	return writeVirtualMemory(vaddr, data, 0, data.length);
    }

    /**
     * Transfer data from the specified array to this process's virtual memory.
     * This method handles address translation details. This method must
     * <i>not</i> destroy the current process if an error occurs, but instead
     * should return the number of bytes successfully copied (or zero if no
     * data could be copied).
     *
     * @param	vaddr	the first byte of virtual memory to write.
     * @param	data	the array containing the data to transfer.
     * @param	offset	the first byte to transfer from the array.
     * @param	length	the number of bytes to transfer from the array to
     *			virtual memory.
     * @return	the number of bytes successfully transferred.
     */
    public int writeVirtualMemory(int vaddr, byte[] data, int offset,
				  int length) {
	Lib.assertTrue(offset >= 0 && length >= 0 && offset+length <= data.length);

	byte[] memory = Machine.processor().getMemory();
	
	if (vaddr < 0 || offset < 0 || length < 0 || offset + length > data.length )
	    return 0;

	int bytesCopied = 0;
	int vpn = Processor.pageFromAddress(vaddr);
	int pageOffset = Processor.offsetFromAddress(vaddr);
	int bytesLeftToCopy  = length;
	int bufferOffset = offset;
   
    int paddr = -1;
    
    
    while(bytesLeftToCopy  > 0){
      
      for(int i = 0; i < pageTable.length; i++){
        if(pageTable[i].vpn == vpn && pageTable[i].valid){
          paddr = pageTable[i].ppn * pageSize;
          break;
        }
      }
      
      if(paddr < 0 || paddr >= memory.length){
        return bytesCopied;
      }
      int bytesToEndOfPage = pageSize - pageOffset;
      int bytesToCopy = Math.min(bytesLeftToCopy , bytesToEndOfPage);
      System.arraycopy(data, bufferOffset, memory, paddr, bytesToCopy);
      bytesCopied += bytesToCopy;
      bufferOffset += bytesToCopy;
      bytesLeftToCopy  -= bytesToCopy;
      vpn++;
      pageOffset = 0;
    }

return bytesCopied;
    }

    /**
     * Load the executable with the specified name into this process, and
     * prepare to pass it the specified arguments. Opens the executable, reads
     * its header information, and copies sections and arguments into this
     * process's virtual memory.
     *
     * @param	name	the name of the file containing the executable.
     * @param	args	the arguments to pass to the executable.
     * @return	<tt>true</tt> if the executable was successfully loaded.
     */
    private boolean load(String name, String[] args) {
	Lib.debug(dbgProcess, "UserProcess.load(\"" + name + "\")");
	
	OpenFile executable = ThreadedKernel.fileSystem.open(name, false);
	if (executable == null) {
	    Lib.debug(dbgProcess, "\topen failed");
	    return false;
	}

	try {
	    coff = new Coff(executable);
	}
	catch (EOFException e) {
	    executable.close();
	    Lib.debug(dbgProcess, "\tcoff load failed");
	    return false;
	}

	// make sure the sections are contiguous and start at page 0
	numPages = 0;
	for (int s=0; s<coff.getNumSections(); s++) {
	    CoffSection section = coff.getSection(s);
	    if (section.getFirstVPN() != numPages) {
		coff.close();
		Lib.debug(dbgProcess, "\tfragmented executable");
		return false;
	    }
	    numPages += section.getLength();
	}

	// make sure the argv array will fit in one page
	byte[][] argv = new byte[args.length][];
	int argsSize = 0;
	for (int i=0; i<args.length; i++) {
	    argv[i] = args[i].getBytes();
	    // 4 bytes for argv[] pointer; then string plus one for null byte
	    argsSize += 4 + argv[i].length + 1;
	}
	if (argsSize > pageSize) {
	    coff.close();
	    Lib.debug(dbgProcess, "\targuments too long");
	    return false;
	}

	// program counter initially points at the program entry point
	initialPC = coff.getEntryPoint();	

	// next comes the stack; stack pointer initially points to top of it
	numPages += stackPages;
	initialSP = numPages*pageSize;

	// and finally reserve 1 page for arguments
	numPages++;

	if (!loadSections())
	    return false;

	// store arguments in last page
	int entryOffset = (numPages-1)*pageSize;
	int stringOffset = entryOffset + args.length*4;

	this.argc = args.length;
	this.argv = entryOffset;
	
	for (int i=0; i<argv.length; i++) {
	    byte[] stringOffsetBytes = Lib.bytesFromInt(stringOffset);
	    Lib.assertTrue(writeVirtualMemory(entryOffset,stringOffsetBytes) == 4);
	    entryOffset += 4;
	    Lib.assertTrue(writeVirtualMemory(stringOffset, argv[i]) ==
		       argv[i].length);
	    stringOffset += argv[i].length;
	    Lib.assertTrue(writeVirtualMemory(stringOffset,new byte[] { 0 }) == 1);
	    stringOffset += 1;
	}

	return true;
    }

    /**
     * Allocates memory for this process, and loads the COFF sections into
     * memory. If this returns successfully, the process will definitely be
     * run (this is the last step in process initialization that can fail).
     *
     * @return	<tt>true</tt> if the sections were successfully loaded.
     */
    protected boolean loadSections() {
		if (numPages > Machine.processor().getNumPhysPages()) {
		    coff.close();
		    Lib.debug(dbgProcess, "\tinsufficient physical memory");
		    return false;
		}

		LinkedList<Integer> pages = UserKernel.allocatePages(numPages);
		if(pages != null){
			pageTable = new TranslationEntry[numPages];
			for (int i = 0; i < pages.size(); ++i) {
					pageTable[i] = new TranslationEntry(i, pages.removeFirst(), true,false,false,false);

				}

			// load sections
			for (int s=0; s<coff.getNumSections(); s++) {
			    CoffSection section = coff.getSection(s);
			    
			    Lib.debug(dbgProcess, "\tinitializing " + section.getName()
				      + " section (" + section.getLength() + " pages)");

			    for (int i=0; i<section.getLength(); i++) {
					int vpn = section.getFirstVPN()+i;

					TranslationEntry entry = pageTable[vpn];
					entry.readOnly = section.isReadOnly();
					int ppn = entry.ppn;
					section.loadPage(i, ppn);
			    }
			}
			
			return true;
		}
    }

    /**
     * Release any resources allocated by <tt>loadSections()</tt>.
     */
    protected void unloadSections() {
    	LinkedList<Integer> allocatedPages = new LinkedList<Integer>();
    	allocatedPages.addAll(pageTable);
    	UserKernel.deallocatePages(allocatedPages);
    	coff.close();
    }    

    /**
     * Initialize the processor's registers in preparation for running the
     * program loaded into this process. Set the PC register to point at the
     * start function, set the stack pointer register to point at the top of
     * the stack, set the A0 and A1 registers to argc and argv, respectively,
     * and initialize all other registers to 0.
     */
    public void initRegisters() {
	Processor processor = Machine.processor();

	// by default, everything's 0
	for (int i=0; i<processor.numUserRegisters; i++)
	    processor.writeRegister(i, 0);

	// initialize PC and SP according
	processor.writeRegister(Processor.regPC, initialPC);
	processor.writeRegister(Processor.regSP, initialSP);

	// initialize the first two argument registers to argc and argv
	processor.writeRegister(Processor.regA0, argc);
	processor.writeRegister(Processor.regA1, argv);
    }

    /**
     * Handle the halt() system call. 
     */
    private int handleHalt() {

    	if(this != UserKernel.root){
    		return 0;
    	}
    	
		Machine.halt();
		
		Lib.assertNotReached("Machine.halt() did not halt machine!");
		return 0;
    }


    private static final int
        syscallHalt = 0,
	syscallExit = 1,
	syscallExec = 2,
	syscallJoin = 3,
	syscallCreate = 4,
	syscallOpen = 5,
	syscallRead = 6,
	syscallWrite = 7,
	syscallClose = 8,
	syscallUnlink = 9;

    /**
     * Handle a syscall exception. Called by <tt>handleException()</tt>. The
     * <i>syscall</i> argument identifies which syscall the user executed:
     *
     * <table>
     * <tr><td>syscall#</td><td>syscall prototype</td></tr>
     * <tr><td>0</td><td><tt>void halt();</tt></td></tr>
     * <tr><td>1</td><td><tt>void exit(int status);</tt></td></tr>
     * <tr><td>2</td><td><tt>int  exec(char *name, int argc, char **argv);
     * 								</tt></td></tr>
     * <tr><td>3</td><td><tt>int  join(int pid, int *status);</tt></td></tr>
     * <tr><td>4</td><td><tt>int  creat(char *name);</tt></td></tr>
     * <tr><td>5</td><td><tt>int  open(char *name);</tt></td></tr>
     * <tr><td>6</td><td><tt>int  read(int fd, char *buffer, int size);
     *								</tt></td></tr>
     * <tr><td>7</td><td><tt>int  write(int fd, char *buffer, int size);
     *								</tt></td></tr>
     * <tr><td>8</td><td><tt>int  close(int fd);</tt></td></tr>
     * <tr><td>9</td><td><tt>int  unlink(char *name);</tt></td></tr>
     * </table>
     * 
     * @param	syscall	the syscall number.
     * @param	a0	the first syscall argument.
     * @param	a1	the second syscall argument.
     * @param	a2	the third syscall argument.
     * @param	a3	the fourth syscall argument.
     * @return	the value to be returned to the user.
     */
    public int handleSyscall(int syscall, int a0, int a1, int a2, int a3) {
    	String filename = "";
    	if(syscall == syscallCreate || syscall == syscallOpen || syscall == syscallUnlink
    			|| syscall == syscallExec){
    		filename = readVirtualMemoryString(a0, 256);
    	}
    	switch (syscall) {
			case syscallHalt:
			    return handleHalt();
			case syscallExit:
				syscallExit(a0);
			case syscallExec:
				return syscallExec(filename, a1, a2);
			case syscallJoin:
				return syscallJoin(a0, a1);
			case syscallCreate:
				return syscallCreate(filename);
			case syscallOpen:
				return syscallOpen(filename);
			case syscallRead:
				return syscallRead(a0, a1, a2);
			case syscallWrite:
				return syscallWrite(a0, a1, a2);
			case syscallClose:
				return syscallClose(a0);
			case syscallUnlink:
				return syscallUnlink(filename);
			default:
			    Lib.debug(dbgProcess, "Unknown syscall " + syscall);
			    Lib.assertNotReached("Unknown system call!");
		}
		return 0;
    }
    
    private void syscallExit(int status){
    	
    	unloadSections();
    	for(int i = 2; i < fileTable.length; i++){
			if(fileTable[i] != null){
				fileTable[i].close();
			}
		}
    	for(int i = 0; i < UserKernel.processTable.length; i++){
    		if(UserKernel.processTable[i] != null &&
    				UserKernel.processTable[i].parentProcess == this){
    			UserKernel.processTable[i].parentProcess = null;
    		}
    	}
    	exitStatus = status;
    	
    	boolean isEmpty = true;
    	for(int i = 0; i < UserKernel.processTable.length; i++){
    		if(UserKernel.processTable[i] != null){
    			isEmpty = false;
    			break;
    		}
    	}
    	if(isEmpty){
    		Machine.halt();
    	}
    	
    }
    
    private int syscallExec(String filename, int argc, int argvAddr){
    	
    	if(argc < 1){
    		return -1;
    	}
    	String extension = filename.toLowerCase().substring(filename.length() - 5);
    	if(!extension.equals(".coff")){
    		return -1;
    	}
    	
    	conditionLock.acquire();
    	while(!conditionLock.isHeldByCurrentThread()){
    		//Wait
    	}
    	UserProcess childProcess = UserProcess.newUserProcess();
    	for(int i = 0; i < UserKernel.processTable.length; i++){
    		if(UserKernel.processTable[i] == null){
    			UserKernel.processTable[i] = childProcess;
    			childProcess.processID = UserKernel.nextProcessID;
    			UserKernel.nextProcessID++;
    			childProcess.parentProcess = this;
    			break;
    		}
    	}
    	String[] args = new String[argc];
    	byte[] buffer = new byte[4];
    	for(int i = 0; i < argc; i++){
    		readVirtualMemory(argvAddr + (i * 4), buffer);
    		args[i] = readVirtualMemoryString(Lib.bytesToInt(buffer, 0), 256);
    	}
    	childProcess.execute(filename, args);
    	conditionLock.release();
    	
    	return childProcess.processID;
    	
    }
    
    private int syscallJoin(int pidToJoin, int statusAddr){
    	
    	saveState();
    	
    	UserProcess child = null;
    	boolean canJoin = false;
    	for(int i = 0; i < UserKernel.processTable.length; i++){
    		if(UserKernel.processTable[i] != null &&
    				UserKernel.processTable[i].parentProcess == this &&
    				UserKernel.processTable[i].processID == pidToJoin){
    			child = UserKernel.processTable[i];
    			canJoin = true;
    			break;
    		}
    	}
    	if(!canJoin){
    		return -1;
    	}
    	child.parentProcess = null;
    	if(child.exitStatus == -2){
    		child.syscallExit(0);
    	}
    	byte[] buffer = Lib.bytesFromInt(child.exitStatus);
    	writeVirtualMemory(statusAddr, buffer);
    	
    	restoreState();
    	
    	return child.exitStatus;
    	
    }
    
    private int syscallCreate(String filename){
    	
    	OpenFile file = Machine.stubFileSystem().open(filename, true);
    	if(file != null){
    		for(int i = 2; i < fileTable.length; i++){
    			if(fileTable[i] == null){
    				fileTable[i] = file;
    				return i;
    			}
    		}
    	}
    	return -1;
    	
    }
    
    private int syscallOpen(String filename){
    	
    	OpenFile file = Machine.stubFileSystem().open(filename, false);
    	if(file != null){
    		for(int i = 2; i < fileTable.length; i++){
    			if(fileTable[i] == null){
    				fileTable[i] = file;
    				return i;
    			}
    		}
    	}
    	return -1;
    	
    }
    
    private int syscallRead(int fileDesc, int bufferAddr, int count){
    	
    	OpenFile file = fileTable[fileDesc];
    	if(file == null){
    		return -1;
    	}
    	byte[] buffer = new byte[count];
    	int bytesRead = file.read(buffer, 0, count);
    	writeVirtualMemory(bufferAddr, buffer);
    	return bytesRead;
    	
    }
    
    private int syscallWrite(int fileDesc, int bufferAddr, int count){
    	
    	OpenFile file = fileTable[fileDesc];
    	if(file == null){
    		return -1;
    	}
    	byte[] buffer = new byte[count];
    	readVirtualMemory(bufferAddr, buffer);
    	int bytesWritten = file.write(buffer, 0, count);
    	return bytesWritten;
    	
    }
    
    private int syscallClose(int fileDesc){
    	
    	OpenFile file = fileTable[fileDesc];
    	if(file != null){
    		file.close();
    		fileTable[fileDesc] = null;
    		return 0;
    	}
    	return -1;
    	
    }
    
    private int syscallUnlink(String filename){
    	
    	OpenFile file = Machine.stubFileSystem().open(filename, false);
    	for(int i = 2; i < fileTable.length; i++){
    		if(fileTable[i] == file){
    			fileTable[i].close();
    		}
    	}
    	if(ThreadedKernel.fileSystem.remove(filename)){
    		return 0;
    	}
    	return -1;
    	
    }

    /**
     * Handle a user exception. Called by
     * <tt>UserKernel.exceptionHandler()</tt>. The
     * <i>cause</i> argument identifies which exception occurred; see the
     * <tt>Processor.exceptionZZZ</tt> constants.
     *
     * @param	cause	the user exception that occurred.
     */
    public void handleException(int cause) {
	Processor processor = Machine.processor();

	switch (cause) {
	case Processor.exceptionSyscall:
	    int result = handleSyscall(processor.readRegister(Processor.regV0),
				       processor.readRegister(Processor.regA0),
				       processor.readRegister(Processor.regA1),
				       processor.readRegister(Processor.regA2),
				       processor.readRegister(Processor.regA3)
				       );
	    processor.writeRegister(Processor.regV0, result);
	    processor.advancePC();
	    break;				       
				       
	default:
	    Lib.debug(dbgProcess, "Unexpected exception: " +
		      Processor.exceptionNames[cause]);
	    Lib.assertNotReached("Unexpected exception");
	}
    }

    /** The program being run by this process. */
    protected Coff coff;

    /** This process's page table. */
    protected TranslationEntry[] pageTable;
    /** The number of contiguous pages occupied by the program. */
    protected int numPages;

    /** The number of pages in the program's stack. */
    protected final int stackPages = 8;
    
    private int initialPC, initialSP;
    private int argc, argv;
	
    private static final int pageSize = Processor.pageSize;
    private static final char dbgProcess = 'a';
}
