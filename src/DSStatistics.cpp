#include "DSStatistics.h"

#ifdef DSDTATISTICS_H


/*
 * *****************************************************************************
 *
 * Constrictors and destructor
 *
 * *****************************************************************************
 */

DSStatistics::DSStatistics(int lx, int ly, char*fn_s, char*fn_t)
{
    Lx = lx;
    Ly = ly;

    N = (Lx * Ly) / 5 ;

    // Allocation of memory for histogram arrays
    t = new int[Ly+1];
    for (int i=0; i<Ly+1; i++) t[i] = 0;
    s = new int[N];
    for (int i=0; i<N; i++) s[i] = 0;

    // Initialization
    smin = 0;
    smax = 0;
    tmin = 0;
    tmax = 0;
    iarea = 0;
    area = 0.0;
    for (int i=0; i<N_Q_MOMENTS; i++) {
        t_moments[i]=0;
        s_moments[i]=0;
    }

    // Setting file names
    fn_s_histogram = fn_s;
    fn_t_histogram = fn_t;
}

DSStatistics::~DSStatistics()
{
    delete [] s;
    delete [] t;
    fprintf(stderr,"Deleting Statistics\n");
}



/*
 * *****************************************************************************
 *
 * Simulation
 *
 * *****************************************************************************
 */

void DSStatistics::Simulate(ADSModel*ads)
{
    int tic = time(NULL);
    int toc;
    /* Generating data */
    for (int i=0; i<N_STATISTICS; i++)
    {
        // Some debug information
        if (!(i % 1000))
        {
            toc = time(NULL);
            printf("Simulation iteration #%d, time=%ds\n", i, toc-tic);
        }
        ads->Randomize();
        ads->NextStep();
        ss = ads->Get_s();
        tt = ads->Get_t();
        if ( tt < Ly ) {
            if (ss >= N)
            {
                fprintf(stderr,"s-histogram array exceeded!\n");
                exit(1);
            }
            t[tt]++;
            s[ss]++;
            tmin += ((tt-tmin) & ((tt-tmin) >> 31));
            smin += ((ss-smin) & ((ss-smin) >> 31));
            tmax -= ((tmax-tt) & ((tmax-tt) >> 31));
            smax -= ((smax-ss) & ((smax-ss) >> 31));
            iarea++;
        }
    }
    area = (double) iarea;
}

/*
 * *****************************************************************************
 *
 * Write histograms to file
 *
 * *****************************************************************************
 */

void DSStatistics::WriteHistograms()
{
    FILE*f_t_histogram = fopen(fn_t_histogram, "w");
    FILE*f_s_histogram = fopen(fn_s_histogram, "w");
    /* Open output files to export histograms data */
    if (!f_s_histogram) perror("Unable to open s_histogram file.");
    if (!f_t_histogram) perror("Unable to open t_histogram file.");

    /* Writing histogram data to corresponding files */
    /* i=1 because there is no avalanches of length 0 */
    for (int i=0; i<Ly; i++)
    {
        fprintf(f_t_histogram, "%d %d %lf\n", i+1, t[i], t[i]/area);
    }
    for (int i=0; i<N; i++)
    {
        fprintf(f_s_histogram, "%d %d %lf\n", i+1, s[i], s[i]/area);
    }
    /* Close histogram files */
    fclose(f_s_histogram);
    fclose(f_t_histogram);
}


void DSStatistics::Set_s_hist_filename(char*fname) {
    fn_s_histogram = fname;
}

void DSStatistics::Set_t_hist_filename(char*fname) {
    fn_t_histogram = fname;
}


/*
 * *****************************************************************************
 *
 * q-moments
 *
 * *****************************************************************************
 */

void DSStatistics::Qmoments()
{
    double qmin = 0.0;
    double qh = 0.2;

    double p;
    for (int i=0; i<N_Q_MOMENTS; i++)
    {
        t_moments[i] = 0.0;
        s_moments[i] = 0.0;
        q[i] = qmin + qh*i;

        for (int k=0; k<Ly; k++)
        {
            p = t[k] / area;
            t_moments[i] += pow(1.0*(k+1), q[i]) * p * p;
        }

        for (int k=0; k<N; k++)
        {
            p = s[k] / area;
            s_moments[i] += pow(1.0*(k+1), q[i]) * p * p;
        }

        t_moments[i] = log(t_moments[i]);
        s_moments[i] = log(s_moments[i]);
    }
}


/*
 * *****************************************************************************
 *
 * Data obtaining functions
 *
 * *****************************************************************************
 */

int DSStatistics::Get_smin() {
    return smin;
}
int DSStatistics::Get_smax() {
    return smax;
}
int DSStatistics::Get_tmin() {
    return tmin;
}
int DSStatistics::Get_tmax() {
    return tmax;
}
int DSStatistics::Get_iarea() {
    return iarea;
}
double DSStatistics::Get_area() {
    return area;
}
int DSStatistics::Get_N() {
    return N;
}

double *DSStatistics::Get_t_moments()
{
    return t_moments;
}

double *DSStatistics::Get_s_moments()
{
    return s_moments;
}


#endif  /* DSDTATISTICS_H */
