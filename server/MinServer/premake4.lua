solution "FWServer" 
    language "C++"  
    configurations {"Release","Debug"}
    linkoptions {"-lrt"}


    flags {"ExtraWarnings"}

    configuration "Release"
        flags {"OptimizeSpeed"}

    configuration "Debug"
        defines {"_DEBUG","DEBUG"}
        flags {"Symbols"}       



    project "MinServer" 
        location "./MinServer"
        kind "SharedLib"
        targetname "rhynn-minserver"        
        files {
            "./MinServer/**.h","./MinServer/**.cpp"
        }
        postbuildcommands {"cp -f ../librhynn-minserver.so /usr/lib"}


    project "FWUtil"    
        location "./FWUtil"
        kind "SharedLib"
        targetname "rhynn-fwutil"       
        includedirs {
            "/usr/include/mysql",
            "/usr/local/include/mysql++",
        }
        files {
            "./FWUtil/**.h","./FWUtil/**.cpp"
        }
        postbuildcommands {"cp -f ../librhynn-fwutil.so /usr/lib"}

    project "FWWorld"
        location "./FWWorld"    
        kind "SharedLib"
        targetname "rhynn-fwworld"      
        includedirs {
            "./FWUtil",
            "/usr/include/mysql",
            "/usr/local/include/mysql++",
        }
        libdirs {
            "/usr/local/lib"
        }
        links {
            "FWUtil",
            "mysqlpp",          
            "mysqlclient",
            "boost_regex"
        }
        files {
            "./FWWorld/**.h","./FWWorld/**.cpp"
        }   
        postbuildcommands {"cp -f ../librhynn-fwworld.so /usr/lib"}


    project "FWServer"
        kind "SharedLib"
        location "./FWWServer"
        targetname "rhynn-fwserver"     
        includedirs { 
            "./MinServer",
            "./FWUtil",
            "./FWWorld",
            "/usr/include/mysql",
            "/usr/local/include/mysql++",
            "/usr/local/include"
        }
        libdirs {
            "/usr/local/lib"
        }
        links {
            "MinServer",            
            "FWUtil",
            "FWWorld",
            "mysqlclient",  
            "mysqlpp",          
            "boost_regex"
        }
        files {
            "./FWServer/**.h","./FWServer/**.cpp"
        }
        postbuildcommands {"cp -f ../librhynn-fwserver.so /usr/lib"}

    project "FWServerLauncher"  
        kind "ConsoleApp"
        location "./FWServerLauncher"
        targetdir "."
        targetname "ServerStart"
        includedirs { 
            "./FWUtil",
            "./MinServer",
            "./FWWorld",
            "./FWServer",
            "/usr/include/mysql",
            "/usr/local/include/mysql++",
            "/usr/local/include"
        }
        libdirs {
            "/usr/local/lib"
        }
        links {
            "MinServer",            
            "FWUtil",
            "FWWorld",
            "FWServer",
            "mysqlclient",          
            "mysqlpp",          
            "boost_regex"
        }
        files {
            "./FWServerLauncher/**.h","./FWServerLauncher/**.cpp"
        }


    project "FWShutdown"
        kind "ConsoleApp"
        location "./FWShutdown" 
        targetdir "."
        targetname "ServerStop"
        includedirs {
            "./MinServer"
        }
        libdirs {
            "/usr/local/lib"
        }
        links {
            "MinServer",
        }
        files {
            "./FWShutdown/**.h","./FWShutdown/**.cpp"
        }