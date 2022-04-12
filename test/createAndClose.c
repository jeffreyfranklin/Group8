#include "syscall.h"
#include "stdio.h"

int main(){
	
	char filename1[] = {'c','r','e','a','t','1','.','t','x','t'};
	char *name1 = &filename1[0];
	int fileDesc1 = creat(name1);
	printf("Create returned " + fileDesc1);
	
	int closeVal = close(fileDesc1);
	printf("\nClose returned " + closeVal);
	
	char filename2[] = {'c','r','e','a','t','2','.','t','x','t'};
	char *name2 = &filename2[0];
	int fileDesc2 = creat(name2);
	printf("\nCreate returned " + fileDesc2);
	
	exit(0);
	
}
