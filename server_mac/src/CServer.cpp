#undef UNICODE

#define WIN32_LEAN_AND_MEAN

#include <windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include <stdlib.h>
#include <stdio.h>
#include <string>
#include "DngProfile.h"
#include "CustomMatrix.h"
#include "DngWriter.h"


using namespace std;

// Need to link with Ws2_32.lib
#pragma comment (lib, "Ws2_32.lib")
// #pragma comment (lib, "Mswsock.lib")

#define DEFAULT_BUFLEN 50000
#define DEFAULT_PORT "1111"

DngProfile * getDngP9()
{
    DngProfile * prof = new DngProfile();
    prof->blacklevel = new float[4] {0,0,0,0};
    prof->whitelevel = 1023;
    prof->rawheight = 500;
    prof->rawwidht = 500;
    prof->bayerformat = "rggb";
    prof->rawType = 5;
    prof->rowSize = 0;

    return prof;
}

CustomMatrix * getP9Matrix()
{
    CustomMatrix * matrix = new CustomMatrix();
    matrix->colorMatrix1 = new float[9]{1.068359018f ,-0.2988280055f, -0.1425780054f, -0.4316410122f, 1.355468989f, 0.05078099996f, -0.1015620004f, 0.2441409972f, 0.5859379766f};
    matrix->colorMatrix2 = new float[9]{2.209960938f, -1.332031012f, -0.1416019942f, -0.1894530054f, 1.227538943f, -0.05468700037f, -0.02343700036f, 0.1826169934f, 0.6035159824f};
    matrix->neutralColorMatrix = new float[3]{1, 1, 1};
    return matrix;
}

int __cdecl main(void)
{
    WSADATA wsaData;
    int iResult;

    SOCKET ListenSocket = INVALID_SOCKET;
    SOCKET ClientSocket = INVALID_SOCKET;
    printf("get dng profile \n");
    DngProfile * profile = getDngP9();

    struct addrinfo *result = NULL;
    struct addrinfo hints;

    int iSendResult;
    char recvbuf[DEFAULT_BUFLEN];
    int recvbuflen = DEFAULT_BUFLEN;
    int IMAGEDATALENGTH = 500*500 * 2;
    unsigned char imagedata[500*500 * 2];
    int byteCount = 0;
    int imagesRecieved = 0;
    long totalbytes = 0;

    printf("get matrix \n");
    CustomMatrix * customMatrix = getP9Matrix();

    printf("Init dng writer\n");
    DngWriter *dngw = new DngWriter();
    printf("DOne Init dng writer\n");
    dngw->dngProfile = profile;
    dngw->customMatrix = customMatrix;
    // Initialize Winsock
    iResult = WSAStartup(MAKEWORD(2,2), &wsaData);
    if (iResult != 0) {
        printf("WSAStartup failed with error: %d\n", iResult);
        return 1;
    } else
        printf("WSAStartup\n");

    ZeroMemory(&hints, sizeof(hints));
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_protocol = IPPROTO_TCP;
    hints.ai_flags = AI_PASSIVE;

    // Resolve the server address and port
    iResult = getaddrinfo(NULL, DEFAULT_PORT, &hints, &result);
    if ( iResult != 0 ) {
        printf("getaddrinfo failed with error: %d\n", iResult);
        WSACleanup();
        return 1;
    }

    // Create a SOCKET for connecting to server
    ListenSocket = socket(result->ai_family, result->ai_socktype, result->ai_protocol);
    if (ListenSocket == INVALID_SOCKET) {
        printf("socket failed with error: %ld\n", WSAGetLastError());
        freeaddrinfo(result);
        WSACleanup();
        return 1;
    }

    // Setup the TCP listening socket
    iResult = bind( ListenSocket, result->ai_addr, (int)result->ai_addrlen);
    if (iResult == SOCKET_ERROR) {
        printf("bind failed with error: %d\n", WSAGetLastError());
        freeaddrinfo(result);
        closesocket(ListenSocket);
        WSACleanup();
        return 1;
    }

    freeaddrinfo(result);

    iResult = listen(ListenSocket, SOMAXCONN);
    if (iResult == SOCKET_ERROR) {
        printf("listen failed with error: %d\n", WSAGetLastError());
        closesocket(ListenSocket);
        WSACleanup();
        return 1;
    }

    // Accept a client socket
    ClientSocket = accept(ListenSocket, NULL, NULL);
    if (ClientSocket == INVALID_SOCKET) {
        printf("accept failed with error: %d\n", WSAGetLastError());
        closesocket(ListenSocket);
        WSACleanup();
        return 1;
    } else
        printf("Client connected\n");

    // No longer need server socket
    closesocket(ListenSocket);
    // Receive until the peer shuts down the connection
    do {

        iResult = recv(ClientSocket, recvbuf, recvbuflen, 0);
        if (iResult > 0) {
            printf("Bytes received: %d\n", iResult);
            totalbytes += iResult;

            for (int i = 0; i < iResult; i++) {
                imagedata[byteCount++] = recvbuf[i];
            }

            if(byteCount == IMAGEDATALENGTH){
                string fname = string("img_") + to_string(imagesRecieved++) + string(".dng");
                dngw->bayerBytes = imagedata;
                dngw->rawSize = IMAGEDATALENGTH;
                dngw->fileSavePath = new char[fname.size()];
                strcpy(dngw->fileSavePath, fname.c_str());
                fname.clear();
                printf("File path: %s Size %i\n", dngw->fileSavePath, byteCount);
                dngw->_make = "dngwriter";
                dngw->_model = "model";

                printf("write dng\n");
                dngw->WriteDNG();
                printf("done write dng\n");
                byteCount = 0;
                printf("saved file %s\n", fname);
                printf("totalbytes: %i bytes per img %i \n", totalbytes, (totalbytes/IMAGEDATALENGTH));
            }

        }
        else if (iResult == 0)
            printf("Connection closing...\n");
        else  {
            printf("recv failed with error: %d\n", WSAGetLastError());
            closesocket(ClientSocket);
            WSACleanup();
            return 1;
        }

    } while (iResult > 0);
    // shutdown the connection since we're done
    iResult = shutdown(ClientSocket, SD_SEND);
    if (iResult == SOCKET_ERROR) {
        printf("shutdown failed with error: %d\n", WSAGetLastError());
        closesocket(ClientSocket);
        WSACleanup();
        return 1;
    }

    // cleanup
    closesocket(ClientSocket);
    WSACleanup();

    return 0;
}