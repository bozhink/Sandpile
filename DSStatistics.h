/*
 * File:   DSDtatistics.h
 * Author: bozhin
 *
 * Created on November 3, 2012, 12:18 PM
 */

#ifndef DSDTATISTICS_H
#define	DSDTATISTICS_H
#include "adsmodel.h"
#include <cstdio>
#include <cstdlib>
#include <math.h>

#ifndef N_STATISTICS
#define N_STATISTICS 1000000
#endif  /* N_STATISTICS */
#ifndef N_Q_MOMENTS
#define N_Q_MOMENTS 30 /* Number of calculated q-moments */
#endif /* N_Q_MOMENTS */

class DSStatistics
{
    char*fn_s_histogram;
    char*fn_t_histogram;

    int Lx, Ly; // Lattice sizes
    int N; // Size of s-histogram

    int *t; /* Data array to be stored t-histogram */
    int *s; /* Data array to be stored s-histogram */

    int ss, tt;
    int smin, smax;
    int tmin, tmax;
    double t_area, s_area;
    int iarea;

    double s_moments[N_Q_MOMENTS];
    double t_moments[N_Q_MOMENTS];
    double q[N_Q_MOMENTS];

public:
    DSStatistics(ADSModel*,char*,char*);
    DSStatistics(int,int,char*,char*);
    ~DSStatistics();

    void Simulate(ADSModel*);
    void Simulate(ADSModel*, int);
    void Simulate(ADSModel*, int, int);

    void WriteHistograms();

    void Qmoments();

    void Set_s_hist_filename(char*);
    void Set_t_hist_filename(char*);

    int Get_smin();
    int Get_smax();
    int Get_tmin();
    int Get_tmax();
    int Get_iarea();
    double Get_sarea();
    double Get_tarea();
    int Get_h();
    int Get_N();
    double*Get_s_moments();
    double*Get_t_moments();
};



#endif	/* DSDTATISTICS_H */

