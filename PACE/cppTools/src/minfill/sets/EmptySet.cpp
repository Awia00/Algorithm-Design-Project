#include<functional>
#include "Set.h"
#include<iostream>

namespace minfill::sets {
	template <class T>
	class EmptySet : public Set<T>
	{
		private:
			constexpr EmptySet() {}
			constexpr static const EmptySet<T>& instance = EmptySet<T>();
		public:
			bool IsEmpty() const { return true; }
			bool IsProperSubsetOf(const Set<T>& other) const { return !other.IsEmpty(); }
			bool IsSubsetOf(const Set<T>& other) const { return true; }
			bool Contains(const T& elem) const { return false; }
			int Size() const { return 0; }
			const Set<T>& Add(const T& elem) const { return *this; } // TODO
			const Set<T>& Remove(const T& elem) const { return *this; }
			const Set<T>& Union(const Set<T>& other) const { return other; }
			const Set<T>& Intersect(const Set<T>& other) const { return *this; }
			const Set<T>& Minus(const Set<T>& other) const { return *this; }
			static const Set<T>& GetInstance() { return instance; }
	};
}

using namespace minfill::sets;
using namespace std;

int main(int argc, char* argv[]) {
	const Set<int>& set = EmptySet<int>::GetInstance();

	cout << set.Size() << endl;
}
