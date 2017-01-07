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
jstring sp;					//デバイス名一時保存用

JNIEXPORT void JNICALL Java_JNI_finddevice(JNIEnv *env, jobject obj,jobjectArray device){
	
	//char *argc;
	char *dev;				//デバイス名一時保存用       	
	char errbuf[PCAP_ERRBUF_SIZE];		/* error buffer */
	pcap_if_t *if_list;
	pcap_if_t *next_if;
	int i=0;

	//デバイスをpcap_lookalldevice（）で探す
	if(pcap_findalldevs(&if_list,errbuf)==0){
		next_if = if_list;
		while(next_if != NULL){
			dev = next_if->name;				//デバイス名をdevにいれる
			sp  = (*env)->NewStringUTF(env,dev);		//spにデバイス名をセット
			(*env)->SetObjectArrayElement(env,device,i,sp);//deviceにデバイス名をセット
			i++;
			next_if = next_if->next;			//次のデバイス
		}
	}
	else {
		/* find a capture device if not specified on command-line */
		fprintf(stderr, "Couldn't find default device: %s\n",errbuf);
		exit(EXIT_FAILURE);
	}

	/*ヒープ領域の解放*/	
	pcap_freealldevs(if_list);
	//free(dev);
	//free(dev)を書くと２重解放のエラーが出力される
	return;
	
}
