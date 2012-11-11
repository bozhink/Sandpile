#include "adsmodel.h"

#ifdef ADSMODEL_H

/*
 * *****************************************************************************
 *
 * Constrictors and destructor
 *
 * *****************************************************************************
 */

void ADSModel::construct(int lx, int ly)
{
    srand(time(NULL));
    s=0;
    t=0;
    Lx=lx;
    Ly=ly;
    Lmiddle=Lx/2;
    Lxp1 = Lx + 1;
    Lxm1 = Lx - 1;

    main_matrix = new int* [Lx];
    for (int i=0; i<Lx; i++) main_matrix[i] = new int[Ly];

    for (int i=0; i<Lx; i++)
        for (int j=0; j<Ly; j++)
            main_matrix[i][j] = 0;
    cerr << "Created ADSModel with sizes " << Lx << " by " << Ly << endl;
}

ADSModel::ADSModel () {
    construct(100, 100);
}

ADSModel::ADSModel (int latticeSize) {
    construct(latticeSize, latticeSize);
}

ADSModel::ADSModel (int latticeSizeX, int latticeSizeY) {
    construct(latticeSizeX, latticeSizeY);
}

ADSModel::~ADSModel() {
    for (int i=0; i<Lx; i++)
        delete [] main_matrix[i];
    delete [] main_matrix;
    cerr << "Deleted ADSModel with size " << Lx << " by " << Ly << endl;
}

/*
 * *****************************************************************************
 *
 * Update mathods
 *
 * *****************************************************************************
 */

void ADSModel::update (int x, int y)
{
    if (main_matrix[x][y] >= 3)  /* toppling */
    {
        main_matrix[x][y] -= 3;

        if (y+1>=Ly) {
            return; /* Open boudary condition */
        } else {
            /* Our grain of energy is not on the boundary */
            y++;

            int x1 = (x + Lxp1) % Lx;
            int x2 = (x + Lxm1) % Lx;

            main_matrix[x][y]++;
            main_matrix[x1][y]++;
            main_matrix[x2][y]++;

            update(x,  y);
            update(x1, y);
            update(x2, y);
        }
    }
}


void ADSModel::st_update (int x, int y)
{
    t -= ((t-y) & ((t-y)>>31)); /* Updating t: t=max(t,y) */

    if (main_matrix[x][y] >= 3)  /* toppling */
    {
        s++; /* If there is toppling then size s is incremented */

        main_matrix[x][y] -= 3;

        if (y+1>=Ly) {
            return; /* Open boudary condition */
        } else {
            /* Our grain of energy is not on the boundary */
            y++;

            int x1 = (x + Lx + 1) % Lx;
            int x2 = (x + Lx - 1) % Lx;

            main_matrix[x][y]++;
            main_matrix[x1][y]++;
            main_matrix[x2][y]++;

            st_update(x,  y);
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

int ADSModel::Get_s() {
    return s;
}

int ADSModel::Get_t() {
    return t;
}

void ADSModel::GetSize(int *X, int *Y) {
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

void ADSModel::NextStep()
{
    s=0;
    t=0;
    main_matrix[Lmiddle][0] = 3;
    st_update(Lmiddle, 0);
}

void ADSModel::NextStep(int skip)
{
    for (int i=0; i<skip; i++)
    {
        main_matrix[Lmiddle][0] = 3;
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

void ADSModel::Randomize()
{
    for (int x=0; x<Lx; x++)
        for (int y=0; y<Ly; y++)
            main_matrix[x][y] = rand() % 3;
}



/*
 * *****************************************************************************
 *
 * Save and read matrix state
 *
 * *****************************************************************************
 */
void ADSModel::SaveMatrixState(char*fname)
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

void ADSModel::ReadMatrixState(char*fname)
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


#endif /* ADSMODEL_H */
