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

	project "FWUtil"	
		location "./FWUtil"
		kind "SharedLib"
		files {
			"../FWUtil/**.h","../FWUtil/**.cpp"
		}
					
	project "MinServer"	
		location "./MinServer"
		kind "SharedLib"
		files {
			"../MinServer/**.h","../MinServer/**.cpp"
		}

	project "FWWorld"
		location "./FWWorld"	
		kind "SharedLib"
		includedirs {
			"/usr/include/mysql",
			"/usr/include/mysql++",
			"../FWUtil"
		}
		libdirs {
			"."
		}
		links {
			"FWUtil",
			"mysqlpp",			
			"mysqlclient",			
			"boost_regex"
		}
		files {
			"../FWWorld/**.h","../FWWorld/**.cpp"
		}	

	project "FWServer"
		kind "ConsoleApp"
		includedirs { 
			"../FWUtil",
			"../MinServer",
			"../FWWorld",
			"/usr/include/mysql",
			"/usr/include/mysql++"
		}
		libdirs {
			".",
		}
		links {
			"FWUtil",
			"FWWorld",
			"MinServer",			
			"mysqlpp",			
			"mysqlclient",			
			"boost_regex"
		}
		files {
			"../FWServer/**.h","../FWServer/**.cpp"
		}
	
	project "FWShutdown"
		--location "./FWShutdown"	
		kind "ConsoleApp"
		includedirs {
			"../MinServer"
		}
		libdirs {
			"."
		}
		links {
			"MinServer",
		}
		files {
			"../FWShutdown/**.h","../FWShutdown/**.cpp"
		}	

