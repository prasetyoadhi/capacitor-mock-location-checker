import { WebPlugin } from '@capacitor/core';
import type { MockLocationCheckerPlugin, CheckMockResult } from './definitions';
export declare class MockLocationCheckerWeb extends WebPlugin implements MockLocationCheckerPlugin {
    goToMockLocationAppDetail(options: {
        packageName: string;
    }): Promise<void>;
    isLocationFromMockProvider(): Promise<Boolean>;
    checkMock(options: {
        whiteList: Array<string>;
    }): Promise<CheckMockResult>;
}
