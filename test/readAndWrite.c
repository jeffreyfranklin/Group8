#include "syscall.h"
#include "stdio.h"

int main(){
	
	char buffer[2];
	char *bufferAddr = &buffer[0];
	
	int readVal = read(fdStandardInput, bufferAddr, 2);
	printf("Read returned " + readVal);
	
	int writeVal = write(fdStandardOutput, bufferAddr, readVal);
	printf("\nWrite returned " + writeVal);
	
	exit(0);
	
}
