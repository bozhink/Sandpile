#include "sdsmodel.h"

#ifdef SDSMODEL_H

/*
 * *****************************************************************************
 *
 * Constrictors and destructor
 *
 * *****************************************************************************
 */

void SDSModel::construct(int lx, int ly)
{
    srand(time(NULL));
    s=0;
    t=0;
    Lx=lx;
    Ly=ly;
    Lmiddle=Lx/2;

    main_matrix = new int* [Lx];
    for (int i=0; i<Lx; i++) main_matrix[i] = new int[Ly];

    for (int i=0; i<Lx; i++)
        for (int j=0; j<Ly; j++)
            main_matrix[i][j] = 0;
    cerr << "Created SDSModel with sizes " << Lx << " by " << Ly << endl;
}

SDSModel::SDSModel () {
    construct(100, 100);
}

SDSModel::SDSModel (int latticeSize) {
    construct(latticeSize, latticeSize);
}

SDSModel::SDSModel (int latticeSizeX, int latticeSizeY) {
    construct(latticeSizeX, latticeSizeY);
}

SDSModel::~SDSModel() {
    for (int i=0; i<Lx; i++)
        delete [] main_matrix[i];
    delete [] main_matrix;
    cerr << "Deleted SDSModel with size " << Lx << " by " << Ly << endl;
}

/*
 * *****************************************************************************
 *
 * Update mathods
 *
 * *****************************************************************************
 */

void SDSModel::update (int x, int y)
{
    if (main_matrix[x][y] >= 2)  /* toppling */
    {
        main_matrix[x][y] = 0;

        if (y+1>=Ly) {
            return; /* Open boudary condition */
        } else {
            /* Our grain of energy is not on the boundary */
            /* Here goes stochatic part */
            /* Nonexclusive, NESDS */

            y++;
            x += (Lx-1);

            int x1 = (x + rand() % 3) % Lx;
            int x2 = (x + rand() % 3) % Lx;


            main_matrix[x1][y]++;
            main_matrix[x2][y]++;

            update(x1, y);
            update(x2, y);
        }
    }
    //printf("Update: x=%d\ty=%d\tt=%d\ts=%d\n", x, y, t, s);
}


void SDSModel::st_update (int x, int y)
{
    t -= ((t-y) & ((t-y)>>31)); /* Updating t: t=max(t,y) */

    if (main_matrix[x][y] >= 2)  /* toppling */
    {
        s++; /* If there is toppling then size s is incremented */

        main_matrix[x][y] = 0;

        if (y+1>=Ly) {
            return; /* Open boudary condition */
        } else {
            /* Our grain of energy is not on the boundary */
            /* Here goes stochatic part */
            /* Nonexclusive, NESDS */

            y++;
            x += (Lx-1);

            int x1 = (x + rand() % 3) % Lx;
            int x2 = (x + rand() % 3) % Lx;

            main_matrix[x1][y]++;
            main_matrix[x2][y]++;

            st_update(x1, y);
            st_update(x2, y);
        }
    }
}

/*
 * *****************************************************************************
 *
 * Data obtaining functions
 *
 * *****************************************************************************
 */

int SDSModel::Get_s() {
    return s;
}

int SDSModel::Get_t() {
    return t;
}

void SDSModel::GetSize(int *X, int *Y) {
    *X = Lx;
    *Y = Ly;
}


/*
 * *****************************************************************************
 *
 * Next Step functions
 *
 * *****************************************************************************
 */

void SDSModel::NextStep()
{
    s=0;
    t=0;
    main_matrix[Lmiddle][0] = 2;
    st_update(Lmiddle, 0);
}

void SDSModel::NextStep(int skip)
{
    for (int i=0; i<skip; i++)
    {
        main_matrix[Lmiddle][0] = 2;
        update(Lmiddle, 0);
    }
    NextStep();
}


/*
 * *****************************************************************************
 *
 * Termalization functions
 *
 * *****************************************************************************
 */

void SDSModel::Randomize()
{
    for (int x=0; x<Lx; x++)
        for (int y=0; y<Ly; y++)
            main_matrix[x][y] = rand() % 2;
}



/*
 * *****************************************************************************
 *
 * Save and read matrix state
 *
 * *****************************************************************************
 */
void SDSModel::SaveMatrixState(char*fname)
{
    fstream f_save;
    f_save.open(fname, ios::out | ios::trunc | ios::binary);
    if (!f_save.is_open()) perror("Cannot open file to save matrix state");

    int N=1;
    char buf[1];

    for (int y=0; y<Ly; y++)
        for (int x=0; x<Lx; x++)
        {
            sprintf(buf, "%d", main_matrix[x][y]);
            f_save.write(buf, 1);
        }

    f_save.close();
}

void SDSModel::ReadMatrixState(char*fname)
{
    fstream f_read;
    f_read.open(fname, ios::in | ios::binary);
    if (!f_read.is_open())
    {
        fprintf(stderr,"Cannot open file to read matrix state");
        exit(1);
    }
    f_read.seekg(0, ios::beg);

    int N=SAVE_BUFFER_SIZE;
    char buf[SAVE_BUFFER_SIZE];

    for (int y=0; y<Ly; y++)
        for (int x=0; x<Lx; x++)
        {
            f_read.read(buf, SAVE_BUFFER_SIZE);
            main_matrix[x][y] = atoi(buf);
        }

    f_read.close();
}


#endif /* SDSMODEL_H */
