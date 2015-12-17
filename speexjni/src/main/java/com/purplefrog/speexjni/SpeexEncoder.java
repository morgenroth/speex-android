package com.purplefrog.speexjni;

/**
 * A thin wrapper around some JNI wrapped around the Xiph Speex library.
 *
 * The samples read from an {@link android.media.AudioTrack} are compatible with the library.
 *
 * 
 *
 * Copyright 2012, Robert Forsman
 * speex-ndk@thoth.purplefrog.com
 */
public class SpeexEncoder
{
    private final int slot;

    public SpeexEncoder(FrequencyBand band, int quality)
    {
        slot = allocate(band.code, quality);
    }

    @Override
    protected void finalize()
        throws Throwable
    {
        deallocate(slot);
    }

    public synchronized int getFrameSize()
    {
        return getFrameSize(slot);
    }

    /**
     *
     * @param samples must have length == {@link #getFrameSize()}
     * @return a compressed audio frame suitable for use with {@link SpeexDecoder#decode(byte[])}.  Getting it across the network in one piece with the right framing is <i>your</i> problem.
     */
    public synchronized byte[] encode(short[] samples)
    {
        return encode(slot, samples);
    }

    private native static byte[] encode(int slot, short[] samples);
    private native static int getFrameSize(int slot);

    /**
     * allocates a slot in the JNI implementation for our native bits.  Store it in the {@link #slot} field.
     * @param quality from 0 to 10 inclusive, used by the speex library
     * @param band_code 0 = narrowband, 1 = wideband, 2 = ultra-wide band
     * @return an index into a slot array in the JNI implementation for our encoder parameters.
     */
    protected native static int allocate(int band_code, int quality);

    /**
     * @param slot the return value from a previous call to {@link #allocate(int, int)}
     */
    protected native static void deallocate(int slot);

    static {
        System.loadLibrary("speex");
    }

    public static void main(String[] argv)
    {
        short[] bogus = new short[666];

        byte[] frame = new SpeexEncoder(FrequencyBand.WIDE_BAND, 9).encode(bogus);

        System.out.println(frame.length);
    }
}