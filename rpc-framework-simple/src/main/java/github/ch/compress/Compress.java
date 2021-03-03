package github.ch.compress;

import github.ch.extension.SPI;

@SPI
public interface Compress {
    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);
}
