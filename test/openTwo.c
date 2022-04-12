#include "syscall.h"
#include "stdio.h"

int main(){
	
	char filename1[] = {'c','r','e','a','t','1','.','t','x','t'};
	char *name1 = &filename1[0];
	int fileDesc1 = open(name1);
	
	char filename2[] = {'c','r','e','a','t','2','.','t','x','t'};
	char *name2 = &filename2[0];
	int fileDesc2 = open(name2);
	printf("Create returned " + fileDesc1);
	printf(" and " + fileDesc2);
	
	char buffer[10];
	char *bufferAddr = &buffer[0];
	
	int readVal = read(fileDesc1, bufferAddr, 10);
	printf("\nRead returned " + readVal);
	
	int writeVal = write(fileDesc2, bufferAddr, readVal);
	printf("\nWrite returned " + writeVal);
	
	int unlink1 = unlink(name1);
	int unlink2 = unlink(name2);
	printf("\nUnlink returned " + unlink1);
	printf(" and " + unlink2);
	
	exit(0);
	
}
