export interface MockLocationCheckerPlugin {
  /**
   * 
   * @param options {whiteList: Array<string>}
   */
  checkMock(options: { whiteList: Array<string> }): Promise<CheckMockResult>;

  isLocationFromMockProvider(): Promise<Boolean>;

  /**
   * 
   * @param options {packageName: string}
   */
  goToMockLocationAppDetail(options: { packageName: string }): Promise<void>;

  checkMockGeoLocation(): Promise<CheckMockResult>;
}

export interface CheckMockResult {
  isMock: boolean;
  messages?: string;
  indicated?: Array<string>;
}