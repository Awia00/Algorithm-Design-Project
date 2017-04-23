#include <set>
#include <string>
#include <iostream>

int main(int argc, char* argv[]) {
	std::string a,b;

    std::cout << "Graph G {" << std::endl;
	while(std::cin >> a >> b) {
	    std::cout << "\t" << a << "--" << b << std::endl;

	}

	std::cout << "}" << std::endl;
}
