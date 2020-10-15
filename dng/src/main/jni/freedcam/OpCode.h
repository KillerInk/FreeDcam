//
// Created by troop on 21.07.2018.
//

#ifndef FREEDCAM_OPCODE_H
#define FREEDCAM_OPCODE_H


class OpCode {
public:
    unsigned char* op2;
    unsigned char* op3;
    int op2Size = 0;
    int op3Size = 0;

    void clear()
    {
        op2 = nullptr;
        op3 = nullptr;
        op2Size = 0;
        op2Size = 0;
    }

    bool haveOP2()
    {
        return op2Size > 0;
    }

    bool haveOP3()
    {
        return op3Size > 0;
    }
};


#endif //FREEDCAM_OPCODE_H
