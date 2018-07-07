#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// #ifndef SEP
//   #define SEP "/"
// #endif
#if !defined(WINDOWS) && !defined(LINUX)
#define LINUX
#endif

#ifdef WINDOWS
#include <process.h>	
#include "7z.h"
#include "7z_dll.h"
#include "WindowsLauncher.h"
#define MKDIR "mkdir"
#define SEP "\\"
#endif
#ifdef LINUX
#include <unistd.h>
#include "LinuxLauncher.h"
#define MKDIR "mkdir -p"
#define SEP "/"
#endif

#ifndef SHARED_ROOT
#include <time.h>
#define _RND_CHARSET \
  "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
void rand_str(char *dest, size_t length) {
  while (length-- > 0) {
    size_t index = (double)rand() / RAND_MAX * (sizeof _RND_CHARSET - 1);
    *dest++ = _RND_CHARSET[index];
  }
  *dest = '\0';
}
#else
#define STRINGIFY(x) #x
#define TOSTRING(x) STRINGIFY(x)
int fexists(const char *f) {
  FILE *fp;
  if (fp = fopen(f, "r")) {
    fclose(fp);
    return 1;
  }
  return 0;
}
#endif

const char *getTempDir(){
   const char* d=getenv("TMPDIR");
    if(d==NULL){
      d = getenv("TMP");
    }
    if(d==NULL){
      d = getenv("TEMP");
    }
    if(d==NULL){
      d = getenv("TEMPDIR");
    }
    #ifdef LINUX
    if(d==NULL){
      d = "/tmp";
    }
#endif
    return d;
}

int main(int argc,  char **argv) {
  if (argc < 1) {
    printf("Error! Launch this from a shell.");
    return 1;
  }
#ifndef SHARED_ROOT
  srand(time(NULL));
#endif

  char cmd[512];
  const char *tmp = getTempDir();
  if (tmp == NULL) {
    printf("Error! Invalid tmp folder");
    return 1;
  }
#ifdef DEBUG
  printf("System temp folder %s\n", tmp);
  printf("Args:\n");
  for (int i = 0; i < argc;i++){
    printf("%s\n", argv[i]);
  }
#endif

  char root[64];

#ifndef SHARED_ROOT
  char rand[6];
  rand_str(rand, 6);
#else
  char *rand = TOSTRING(SHARED_ROOT);
#endif

  strcpy(root, tmp);
  strcat(root, SEP);
  strcat(root, rand);
  strcat(root, SEP);
  strcat(root, "\0");

#ifdef DEBUG
  printf("Use temp folder %s\n", root);
#endif
  strcpy(cmd, MKDIR);
  strcat(cmd, " \"");
  strcat(cmd, root);
  strcat(cmd, "\"\0");

#ifdef DEBUG
  printf("Cmd %s \n", cmd);
#endif
  system(cmd);

  char Bundle_path[strlen(root) + 32];
#ifdef WINDOWS
  char __7z_exe_path[strlen(root) + 6];
  strcpy(__7z_exe_path, root);
  strcat(__7z_exe_path, "7z.exe");

  char __7z_dll_path[strlen(root) + 6];
  strcpy(__7z_dll_path, root);
  strcat(__7z_dll_path, "7z.dll");

  strcpy(Bundle_path, root);
  strcat(Bundle_path, "windows-bundle.7z");
  strcat(Bundle_path, "\0");

  char WindowsLauncher_bat_path[strlen(root) + 19];
  strcpy(WindowsLauncher_bat_path, root);
  strcat(WindowsLauncher_bat_path, "WindowsLauncher.bat");
#endif
#ifdef LINUX
  strcpy(Bundle_path, root);
  strcat(Bundle_path, "linux-bundle.tar.gz");
  strcat(Bundle_path, "\0");

  char LinuxLauncher_sh_path[strlen(root) + 17];
  strcpy(LinuxLauncher_sh_path, root);
  strcat(LinuxLauncher_sh_path, "LinuxLauncher.sh");
#endif

#ifdef DEBUG
#ifdef WINDOWS
  printf("7z.exe %s\n", __7z_exe_path);
  printf("7z.dll %s\n", __7z_dll_path);
#endif
  printf("bundle.7z %s\n", Bundle_path);
  printf("Launcher %s\n", argv[0]);
#endif

  FILE *out;
  FILE *in;

#ifdef WINDOWS
#ifdef SHARED_ROOT
  if (!fexists(__7z_exe_path)) {
#endif

    out = fopen(__7z_exe_path, "wb");
    fwrite(__7z_exe, 1, __7z_exe_len, out);
    fclose(out);

#ifdef SHARED_ROOT
  }
#endif

#ifdef SHARED_ROOT
  if (!fexists(__7z_dll_path)) {
#endif
    out = fopen(__7z_dll_path, "wb");
    fwrite(__7z_dll, 1, __7z_dll_len, out);
    fclose(out);
#ifdef SHARED_ROOT
  }
#endif

#endif

#ifdef WINDOWS
#ifdef SHARED_ROOT
  if (!fexists(WindowsLauncher_bat_path)) {
#endif
    out = fopen(WindowsLauncher_bat_path, "wb");
    fwrite(WindowsLauncher_bat, 1, WindowsLauncher_bat_len, out);
    fclose(out);
#ifdef SHARED_ROOT
  }
#endif
#endif
#ifdef LINUX
#ifdef SHARED_ROOT
  if (!fexists(LinuxLauncher_sh_path)) {
#endif
    out = fopen(LinuxLauncher_sh_path, "wb");
    fwrite(LinuxLauncher_sh, 1, LinuxLauncher_sh_len, out);
    fclose(out);
#ifdef SHARED_ROOT
  }
#endif
#endif

#ifdef SHARED_ROOT
  if (!fexists(Bundle_path)) {
#endif
    unsigned char chunk[1024 * 1024];
    out = fopen(Bundle_path, "wb");
    in = fopen(argv[0], "rb");
    while (!feof(in)) {
      int read = fread(chunk, 1, 1, in);
      fwrite(chunk, 1, read, out);
    }
    fclose(out);
    fclose(in);
#ifdef SHARED_ROOT
  }
#endif

#ifdef LINUX
  strcpy(cmd, "\"$(which chmod)\"");
  strcat(cmd, " +x \"");
  strcat(cmd, LinuxLauncher_sh_path);
  strcat(cmd, "\"\0");

  system(cmd);
#ifdef DEBUG
  printf("Cmd %s \n", cmd);
#endif
#endif


#ifdef LINUX
  argv[0] = LinuxLauncher_sh_path;
  #ifdef DEBUG
  printf("Exec Args:\n");
  for (int i = 0; i < argc;i++){
    printf("%s\n", argv[i]);
  }
#endif

  execv(LinuxLauncher_sh_path, argv);
#endif
#ifdef WINDOWS
  argv[0] = WindowsLauncher_bat_path;
#ifdef DEBUG
  printf("Exec Args:\n");
  for (int i = 0; i < argc;i++){
    printf("%s\n", argv[i]);
  }
#endif
 spawnv(P_WAIT,WindowsLauncher_bat_path, argv);
#endif
  


  return 0;
}