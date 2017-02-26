# TODO LIST

## Base
- [x] DDS format implementation
- [ ] dx10 header implementation
- [x] MipMap generation

## CLI
- [x] Modular CLI

## Uncompressed color models
- [x] ARGB8
- [x] RGB8
- [x] RGB565
- [ ] ... Others  ...


## S3TC
- [x] S3TC DXT1  native compression via EXT_Texture_Compression_S3TC
- [x] S3TC DXT3 native compression via EXT_Texture_Compression_S3TC
- [x] S3TC DXT5 native compression via EXT_Texture_Compression_S3TC
- [ ] S3TC pure-java compression < not possible atm due to sofware patent

## 3DC/RGTC
- [x] 3DC+/RGTC1 native compression via OpenGL 3.0 core
- [x] 3DC/RGTC2 native compression via OpenGL 3.0 core
- [ ] RGTC1 pure-java compression
- [ ] RGTC2 pure-java compression


## S2TC
- [x] S2TC DXT1 pure-java compression
- [ ] S2TC DXT3 pure-java compression
- [ ] S2TC DXT5 pure-java  compression
- [ ] Improve palette generation 
- [ ] Add alpha palette generation

## Unit tests
- [ ] Write unit tests for every format

## Others
- [ ] Implement ETC compression
