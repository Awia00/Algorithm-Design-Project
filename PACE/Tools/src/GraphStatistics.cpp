#include <set>
#include <string>
#include <iostream>

int main(int argc, char* argv[]) {
	std::set<std::string> vertices;
	int edges = 0;

	std::string a,b;

	while(std::cin >> a >> b) {
		vertices.insert(a);
		vertices.insert(b);
		edges++;
	}

	std::cout << "Vertices: " << vertices.size() << "\tedges: " << edges << std::endl;
}
