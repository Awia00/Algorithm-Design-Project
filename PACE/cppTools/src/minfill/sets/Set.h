namespace minfill::sets {
	template<class T>
	class Set {
		public:
			virtual bool IsEmpty() const = 0;
			bool IsProperSubsetOf(const Set<T>& other) const { return Size() < other.Size() && IsSubsetOf(other); }
			virtual bool IsSubsetOf(const Set<T>& other) const = 0;
			virtual bool Contains(const T& elem) const = 0;
			virtual int Size() const = 0;
			virtual const Set<T>& Add(const T& elem) const = 0;
			virtual const Set<T>& Remove(const T& elem) const = 0;
			virtual const Set<T>& Union(const Set<T>& other) const = 0;
			virtual const Set<T>& Intersect(const Set<T>& other) const = 0;
			virtual const Set<T>& Minus(const Set<T>& other) const = 0;
			//static Set<T>& empty() { return 0; }
	};
}
