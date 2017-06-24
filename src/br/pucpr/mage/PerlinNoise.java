package br.pucpr.mage;

import java.util.Random;

/**
 * Created by LaraPorts on 23/06/2017.
 */
public class PerlinNoise {


    public float[][] GetNoise(int width,int height, int seed)
    {
        float[][] whiteNoise = WhiteNoise(width,height,seed);
        return GeneratePerlinNoise(whiteNoise,8);
    }

    public static float[][] GetEmptyArray(int x, int y) {
        return new float[x][y];
    }

    float Interpolate(float x0, float x1, float alpha) {
        return x0 * (1 - alpha) + alpha * x1;
    }

    public float[][] WhiteNoise(int width, int height, int seed){
        Random random = new Random(seed);
        float[][] noise = GetEmptyArray(width, height);
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                noise[i][j] = (float)random.nextDouble() % 1;
            }
        }
        return noise;
    }

    public float[][] SmoothNoise(float[][] baseNoise, int octave){
        int width = baseNoise.length;
        int height = baseNoise[0].length;

        float[][] smoothNoise = GetEmptyArray(width, height);

        int samplePeriod = (int) Math.pow(2, octave);
        float sampleFrequency = 1.0f / samplePeriod;

        for (int i = 0; i < width; i++){
            int sample_i0 = (i/samplePeriod) * samplePeriod;
            int sample_i1 = (sample_i0 + samplePeriod) % width;
            float horizontal_blend = (i-sample_i0) * sampleFrequency;
            for (int j = 0; j < height; j++){
                int sample_j0 = (j / samplePeriod) * samplePeriod;
                int sample_j1 = (sample_j0 + samplePeriod) % height; //wrap around
                float vertical_blend = (j - sample_j0) * sampleFrequency;

                float top = Interpolate(baseNoise[sample_i0][sample_j0],
                        baseNoise[sample_i1][sample_j0], horizontal_blend);

                float bottom = Interpolate(baseNoise[sample_i0][sample_j1],
                        baseNoise[sample_i1][sample_j1], horizontal_blend);

                smoothNoise[i][j] = Interpolate(top, bottom, vertical_blend);
            }
        }
        return smoothNoise;
    }

    float[][] GeneratePerlinNoise(float[][] baseNoise, int octaveCount)
    {
        int width = baseNoise.length;
        int height = baseNoise[0].length;

        float[][][] smoothNoise = new float[octaveCount][][]; //an array of 2D arrays containing

        float persistance = 0.5f;
        for (int i = 0; i < octaveCount; i++)
            smoothNoise[i] = SmoothNoise(baseNoise, i);

        float[][] perlinNoise = new float[width][height];
        float amplitude = 1.0f;
        float totalAmplitude = 0.0f;
        for (int octave = octaveCount - 1; octave >= 0; octave--)
        {
            amplitude *= persistance;
            totalAmplitude += amplitude;
            for (int i = 0; i < width; i++)
                for (int j = 0; j < height; j++)
                    perlinNoise[i][j] += smoothNoise[octave][i][j] * amplitude;
        }
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                perlinNoise[i][j] /= totalAmplitude;

        return perlinNoise;
    }
}
