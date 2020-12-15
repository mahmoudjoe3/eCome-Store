  /// The prefix that is prepended to each of the defined names.
  final String _prefix;

  /// The length of the prefix.
  final int _length;

  /// A table mapping names that are defined in this namespace to the element
  /// representing the thing declared with that name.
  @override
  final Map<String, Element> _definedNames;

  /// Initialize a newly created namespace to have the names resulting from
  /// prefixing each of the [_definedNames] with the given [_prefix] (and a
  /// period).
  PrefixedNamespace(String prefix, this._definedNames)
      : _prefix = prefix,
        _length = prefix.length;

  @override
  Map<String, Element> get definedNames {
    Map<String, Element> definedNames = <String, Element>{};
    _definedNames.forEach((String name, Element element) {
      definedNames["$_prefix.$name"] = element;
    });
    return definedNames;
  }

  @override
  Element get(String name) {
    if (name.length > _length && name.startsWith(_prefix)) {
      if (name.codeUnitAt(_length) == '.'.codeUnitAt(0)) {
        return _definedNames[name.substring(_length + 1)];
      }
    }
    return null;
  }

  @override
  Element getPrefixed(String prefix, String name) {
    if (prefix == _prefix) {
      return _definedNames[name];
    }
    return null;
  }
}

/// A name scope used by the resolver to determine which names are visible at
/// any given point in the code.
abstract class Scope {
  /// The prefix used to mark an identifier as being private to its library.
  static int PRIVATE_NAME_PREFIX = 0x5F;

  /// The suffix added to the declared name of a setter when looking up the
  /// setter. Used to disambiguate between a getter and a setter that have the
  /// same name.
  static String SETTER_SUFFIX = "=";

  /// The name used to look up the method used to implement the unary minus
  /// operator. Used to disambiguate between the unary and binary operators.
  static String UNARY_MINUS = "unary-";

  /// A table mapping names that are defined in this scope to the element
  /// representing the thing declared with that name.
  Map<String, Element> _d