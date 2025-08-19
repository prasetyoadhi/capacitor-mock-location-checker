'use strict';

var core = require('@capacitor/core');

const MockLocationChecker = core.registerPlugin('MockLocationChecker', {
    web: () => Promise.resolve().then(function () { return web; }).then(m => new m.MockLocationCheckerWeb()),
});

class MockLocationCheckerWeb extends core.WebPlugin {
    goToMockLocationAppDetail(options) {
        throw new Error('Method not implemented. options: ' + options);
    }
    isLocationFromMockProvider() {
        throw new Error('Method not implemented.');
    }
    async checkMock(options) {
        throw new Error('Method not implemented. options: ' + options);
    }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    MockLocationCheckerWeb: MockLocationCheckerWeb
});

exports.MockLocationChecker = MockLocationChecker;
//# sourceMappingURL=plugin.cjs.js.map
