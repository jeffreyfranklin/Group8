#include "syscall.h"
#include "stdio.h"

int main(){
	
	char buffer[2];
	char *bufferAddr = &buffer[0];
	
	int readVal = read(20, bufferAddr, 2);
	printf("Read returned " + readVal);
	
	int writeVal = write(20, bufferAddr, readVal);
	printf("\nWrite returned " + writeVal);
	
	halt();
	printf("System did not halt");
	
	exit(0);
	
}
