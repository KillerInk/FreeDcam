#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Dec  6 14:15:58 2019

@author: bene
"""

'''
	Simple socket server using threads
'''
import numpy as np
import socket
import sys
import matplotlib.pyplot as plt
np.set_printoptions(threshold=sys.maxsize)
import time 


def bytes2image(mybytes, startpos = 0, CROP_SIZE=100):
    CROP_SIZE = int(CROP_SIZE)
    oneimage = mydata[startpos:startpos+CROP_SIZE*CROP_SIZE*2]
    dt = np.dtype(np.uint16)
    dt = dt.newbyteorder('<')
    myimage_array = np.frombuffer(oneimage , dtype=dt)
    myimage = np.reshape(myimage_array, (CROP_SIZE,CROP_SIZE))
    return myimage

HOST = ''	# Symbolic name, meaning all available interfaces
PORT = 1111	# Arbitrary non-privileged port
CROP_SIZE = 100/2

server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

#Bind socket to local host and port
try:
	server.bind((HOST, PORT))
except socket.error as msg:
    print('Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1])

	
print('Socket bind complete')

#Start listening on socket
server.listen(10)
print('Socket now listening')

#now keep talking with the client
mydata=[]

#open and read the file after the appending:
f = open("test.txt", "a")

#%%
BUFF_SIZE = 2**8
mydata = b''
myimages = []

dataacquisition = False
mystartflag = b'START'
myendflag = b'END'
imagebytesize = int(CROP_SIZE**2*2)
myimage_iter=0


t1 = time.time()
newimage=False
while True:
    #wait to accept a connection - blocking call
    conn, addr = server.accept()
    print('Connected with ' + addr[0] + ':' + str(addr[1]))    
    #%
    while 1:
        
        #myfile = open('testfile.raw', 'w')
        #mymessage = recvall(conn)
        mymessage = conn.recv(BUFF_SIZE)
        #print(mymessage)

#        if dataacquisition==True:
        mydata += mymessage# print(mymessage)
    
        if mystartflag in mydata:
            # get rid of the start-flag and set dataacquisition flat 
            dataacquisition = True
            print(str(mydata[mydata.find(mystartflag):len(mystartflag)]))
            mydata = mydata[mydata.find(mystartflag)+len(mystartflag):-1]
                        
        if myendflag in mydata:
            # stop the acquisition
            dataacquisition = False
            newimage = True
            print(str(mydata[mydata.find(myendflag):mydata.find(myendflag)+len(myendflag)]))
            #mydata = mydata[0:mydata.find(myendflag)]
            print('done reading picture(s) with:'+str(len(mydata))+'-bytes')

            
            #if(np.mod(len(mydata),2)):
            #    print('Skipping frame')
            #    newimage = False
            #    mydata = b''
            
        #%
        if(len(mydata)>=(imagebytesize) and newimage==True):
            newimage=False
            # cast the image from bytes-stream to numpy array and store it
            myimage = bytes2image(mydata, startpos = 0, CROP_SIZE=CROP_SIZE)
            myimages.append(myimage)
            #plt.imshow(myimage), plt.colorbar(), plt.show()
            print(myimage_iter)
            myimage_iter+=1
            # throw away the first image and restart byte acquisition
            mydata = mydata[imagebytesize:-1]

            print('FPS: ' + str(1/(time.time()-t1)))
            t1 = time.time()


# visualize/process images afterwards 
for i_image in range(len(myimages)):
    plt.imshow(myimages[i_image]), plt.colorbar(), plt.show()

print('client disconnected')

conn.close()
server.close()

