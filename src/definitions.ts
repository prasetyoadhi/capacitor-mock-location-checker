export interface MockLocationCheckerPlugin {
  /**
   * 
   * @param options {whiteList: Array<string>}
   */
  checkMock(options: { whiteList: Array<string> }): Promise<CheckMockResult>;

  isLocationFromMockProvider(): Promise<Boolean>;

}

export interface CheckMockResult {
  isMock: boolean;
  messages?: string;
  indicated?: Array<string>;
}