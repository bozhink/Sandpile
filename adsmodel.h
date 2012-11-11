/*
 * File:   adsmodel.h
 * Author: bozhin
 *
 * Created on November 5, 2012, 9:24 PM
 */

#ifndef ADSMODEL_H
#define	ADSMODEL_H

#include <cstdio>
#include <cstdlib>
#include <ctime>
#include <iostream>
#include <fstream>


#define Z_CRITICAL 3
#define SAVE_BUFFER_SIZE 1

using namespace std;


class ADSModel {
    int s; // Size of the avalanche
    int t; // Length of the avalanche
    int**main_matrix; // Spin Matrix
    int Lx, Ly; // Sizes of the matrix
    int Lmiddle; // Half of Lx
    void update(int, int); // Updates matrix without updating values for s ant t
    void st_update(int, int); // Updates matrix and the values of s and t
    void construct(int, int);
public:
    ADSModel();
    ADSModel(int);
    ADSModel(int,int);
    ~ADSModel();

    void SaveMatrixState(char*);
    void ReadMatrixState(char*);

    int Get_s();
    int Get_t();
    void GetSize(int*,int*);
    void Termalize();
    void Termalize(int);
    void NextStep();
    void NextStep(int);
};




#endif	/* ADSMODEL_H */

