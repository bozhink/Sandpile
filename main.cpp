/*
 * File:   main.cpp
 * Author: bozhin
 *
 * Created on November 5, 2012, 9:24 PM
 */

#include <cstdio>
#include <cstdlib>
#include <ctime>
#include <math.h>
#include <iostream>
#include <fstream>

#define N_Q_MOMENTS 30

#include "adsmodel.h"
#include "DSStatistics.h"


using namespace std;

#define F_S_MOMENTS "s_moments"
#define F_T_MOMENTS "t_moments"

/*
 *
 */
int main(int argc, char** argv)
{
    if (argc < 6)
    {
        fprintf(stderr, "Usage: %s <mode> <L> <s-hist> <t-hist> <istate>\n", argv[0]);
        fprintf(stderr, "Where:\n");
        fprintf(stderr, "    mode    -- 0 or 1:\n");
        fprintf(stderr, "    mode=0  -- calculate and write to <istate> the initial termalized state;\n");
        fprintf(stderr, "    mode=1  -- read from <istate> and use the initial termalized state;\n");
        fprintf(stderr, "    L       -- size of model's matrix;\n");
        fprintf(stderr, "    s-hist  -- name of file to write s-histogram data;\n");
        fprintf(stderr, "    t-hist  -- name of file to write t-histogram data;\n");
        fprintf(stderr, "    istate  -- name of file to write or to read termalizes state data.\n");
        return 1;
    }

    int mode = atoi(argv[1]);
    int L = atoi(argv[2]);
    char*fn_s_histogram = argv[3];
    char*fn_t_histogram = argv[4];
    char*f_istate = argv[5];

    double*s_moments;
    double*t_moments;
    fstream f_s_moments;
    fstream f_t_moments;

    int tic, toc;
    DSStatistics stat(L, L, fn_s_histogram, fn_t_histogram);
    if (1)
    {
        ADSModel sdsmodel(L, L);

        if (mode == 0)
        {
            cout << "Termalization ..." << endl;
            tic = time(NULL);
            sdsmodel.Termalize(L*L);
            toc = time(NULL) - tic;
            cout << "    Termalization time " << toc << endl;
            sdsmodel.SaveMatrixState(f_istate);
        }
        else if (mode == 1)
        {
            cout << "Reading data for initial state ..." << endl;
            sdsmodel.ReadMatrixState(f_istate);
        }
        else
        {
            cout << "Invalid value for parameter mode:\n";
            cout << "   it must be 0 or 1\n";
            return 2;
        }



        cout << "Simulation ..." << endl;
        tic = time(NULL);
        stat.Simulate(&sdsmodel);
        toc = time(NULL) - tic;
        cout << "    Simulation time " << toc << endl;

        cout << endl;
        cout << "Writing histogram files ..."<<endl;
        tic = time(NULL);
        stat.WriteHistograms();
        toc = time(NULL) - tic;
        cout << "    Time to write histogram data " << toc << endl;
    }

    cout << endl;
    cout << "S-histogram size   N=" << stat.Get_N() << endl;
    cout << endl;
    cout << "smin = " << stat.Get_smin() << "\tsmax = " << stat.Get_smax() <<endl;
    cout << "tmin = " << stat.Get_tmin() << "\ttmax = " << stat.Get_tmax() <<endl;
    cout << "s-area = " << stat.Get_sarea() << endl;
    cout << "t-area = " << stat.Get_tarea() << endl;
    cout << "i-area = " << stat.Get_iarea() << endl;

    cout << endl;
    cout << "Calculating q-moments ..." << endl;
    tic = time(NULL);
    stat.Qmoments();
    toc = time(NULL) - tic;
    cout << "    q-moments calculation time " << toc << endl;

    f_s_moments.open(F_S_MOMENTS, ios::out | ios::app);
    if (!f_s_moments.is_open()) perror ("Unable to open s-moments output file");
    f_t_moments.open(F_T_MOMENTS, ios::out | ios::app);
    if (!f_t_moments.is_open()) perror ("Unable to open t-moments output file");

    s_moments = stat.Get_s_moments();
    t_moments = stat.Get_t_moments();

    cout << "Writing q-moments ..." << endl;

    f_s_moments << L << " " << log(L) << " ";
    f_t_moments << L << " " << log(L) << " ";
    for (int i=0; i<N_Q_MOMENTS; i++)
    {
        f_s_moments << " " << s_moments[i];
        f_t_moments << " " << t_moments[i];
    }
    f_s_moments << endl;
    f_t_moments << endl;

    f_s_moments.close();
    f_t_moments.close();


    cout << "END" << endl;
    return 0;
}
