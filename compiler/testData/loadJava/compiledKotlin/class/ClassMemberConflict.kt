//ALLOW_AST_ACCESS
// IGNORE_FIR_METADATA_LOADING_K2
//   Ignore reason: KT-58028
package test

class ConstructorTypeParamClassObjectTypeConflict<test> {
    companion object {
        interface test
    }

    val some: test? = throw Exception()
}

class ConstructorTypeParamClassObjectConflict<test> {
    companion object {
        val test = { 12 }()
    }

    val some = test
}

class TestConstructorParamClassObjectConflict(test: String) {
    companion object {
        val test = { 12 }()
    }

    val some = test
}


class TestConstructorValClassObjectConflict(val test: String) {
    companion object {
        val test = { 12 }()
    }

    val some = test
}

class TestClassObjectAndClassConflict {
    companion object {
        val bla = { 12 }()
    }

    val bla = { "More" }()

    val some = bla
}
