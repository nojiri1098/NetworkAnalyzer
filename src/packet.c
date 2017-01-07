//ヘッダファイルのインクルード
#include <pcap.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "JNI.h"

JNIEXPORT jfloat JNICALL Java_JNI_packet(JNIEnv *env, jobject obj,jintArray i_packet,jobjectArray s_packet){
	char *argc;
	
	/*int型配列timeのアドレスを取得*/
	jint *c_packet=(*env)->GetIntArrayElements(env,i_packet,0);
	jstring sp0,sp1,sp2,sp3,sp4,sp5;

	/* 情報格納用変数*/
	jfloat c_time=0; 
	jint prot2=0;
	jint prot3=0;
	jint prot4=0;
	jint sPort=0;
	jint dPort=0;
	jint length=0;
	char* src  = "192.168.101.1";
	char* dist = "224.34.35.32";
	char* sMac = "dkfjeija;ij";
	char* dMac = "lkdjfjiejwj";
	char* info = "Who has";
	char* ascii= "google";

	//ｓｒｃなどのグローバル変数に他の関数が値を代入しそれをここで配列にいれる
	//文字列情報格納
	sp0 = (*env)->NewStringUTF(env,src);
	sp1 = (*env)->NewStringUTF(env,dist);
	sp2 = (*env)->NewStringUTF(env,sMac);
	sp3 = (*env)->NewStringUTF(env,dMac);
	sp4 = (*env)->NewStringUTF(env,info);
	sp5 = (*env)->NewStringUTF(env,ascii);
	
	(*env)->SetObjectArrayElement(env,s_packet,0,sp0);	
	(*env)->SetObjectArrayElement(env,s_packet,1,sp1);	
	(*env)->SetObjectArrayElement(env,s_packet,2,sp2);	
	(*env)->SetObjectArrayElement(env,s_packet,3,sp3);	
	(*env)->SetObjectArrayElement(env,s_packet,4,sp4);	
	(*env)->SetObjectArrayElement(env,s_packet,5,sp5);	
	
	//int型情報格納
	c_packet[0]=prot2;
	c_packet[1]=prot3;
	c_packet[2]=prot4;
	c_packet[3]=sPort;
	c_packet[4]=dPort;
	c_packet[5]=length;
		
	(*env)->ReleaseIntArrayElements(env,i_packet,c_packet,0);
	return c_time;
}
